package com.devhong.dividend.scraper;

import com.devhong.dividend.dto.Company;
import com.devhong.dividend.dto.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);
}
