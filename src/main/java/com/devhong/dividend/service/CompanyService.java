package com.devhong.dividend.service;

import com.devhong.dividend.dto.Company;
import com.devhong.dividend.dto.ScrapedResult;
import com.devhong.dividend.entity.CompanyEntity;
import com.devhong.dividend.entity.DividendEntity;
import com.devhong.dividend.repository.CompanyRepository;
import com.devhong.dividend.repository.DividendRepository;
import com.devhong.dividend.scraper.Scraper;
import com.devhong.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CompanyService {

    private final Trie<String,String> trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e->e.getName())
                .collect(Collectors.toList());
    }

    public void addAutoCompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autoComplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    @CacheEvict(value = CacheKey.KEY_FINANCE, key = "#result.name")
    public Company deleteCompany(String ticker) {
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        deleteAutoCompleteKeyword(company.getName());

        return Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build();
    }
}
