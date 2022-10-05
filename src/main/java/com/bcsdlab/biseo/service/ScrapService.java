package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.scrap.response.ScrapResponse;

public interface ScrapService {

    ScrapResponse createScrap(Long noticeId);
}
