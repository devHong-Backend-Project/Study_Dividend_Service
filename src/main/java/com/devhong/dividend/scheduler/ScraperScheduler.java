package com.devhong.dividend.scheduler;

import com.devhong.dividend.dto.Company;
import com.devhong.dividend.dto.ScrapedResult;
import com.devhong.dividend.entity.CompanyEntity;
import com.devhong.dividend.entity.DividendEntity;
import com.devhong.dividend.repository.CompanyRepository;
import com.devhong.dividend.repository.DividendRepository;
import com.devhong.dividend.scraper.Scraper;
import com.devhong.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // finance에 해당하는 key값을 모두 지움
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        // 저장된 회사 목록 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 회사마다 배당금 정보 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> "+company.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());
            // 스크래핑한 배당금 정보 중 DB에 없는 정보는 DB에 저장
            scrapedResult.getDividends().stream()
                    .map(e-> new DividendEntity(company.getId(),e))
                    .forEach(e -> {
                        boolean exists = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            dividendRepository.save(e);
                        }
                    });
            // 스크래핑 요청에 대한 rate limit 고려
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                //throw new RuntimeException(e);
            }

        }


    }
}
