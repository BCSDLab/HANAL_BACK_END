package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponse;
import com.bcsdlab.biseo.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping
    @Auth
    @ApiOperation(value = "스크랩 등록", notes = "스크랩을 등록합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<ScrapResponse> createScrap(@RequestParam(value = "noticeId") Long noticeId) {
        return new ResponseEntity<>(scrapService.createScrap(noticeId), HttpStatus.OK);
    }

    @DeleteMapping("/{scrapId}")
    @Auth
    @ApiOperation(value = "스크랩 취소", notes = "스크랩을 취소합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<Void> deleteScrap(@PathVariable(value = "scrapId") Long scrapId) {
        scrapService.deleteScrap(scrapId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
