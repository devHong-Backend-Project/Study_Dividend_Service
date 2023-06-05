package com.devhong.dividend.service;

import com.devhong.dividend.dto.Company;
import com.devhong.dividend.dto.Dividend;
import com.devhong.dividend.dto.ScrapedResult;
import com.devhong.dividend.entity.CompanyEntity;
import com.devhong.dividend.entity.DividendEntity;
import com.devhong.dividend.exception.impl.NoCompanyException;
import com.devhong.dividend.repository.CompanyRepository;
import com.devhong.dividend.repository.DividendRepository;
import com.devhong.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company dividend at DB->" + companyName);
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(), dividends);
    }
}
