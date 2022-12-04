package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.dto.scrap.response.ScrapListDto;
import com.bcsdlab.biseo.dto.scrap.response.ScrapListItemDTO;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponseDTO;
import com.bcsdlab.biseo.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    @Auth
    @ApiOperation(value = "스크랩 목록", notes = "스크랩 목록을 불러옵니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<ScrapListDto> getScrapList(
        @ApiParam(name = "searchBy", value = "제목 검색") @RequestParam(value = "searchBy", required = false) String searchBy,
        @ApiParam(name = "cursor", value = "커서 기반 페이지네이션 사용") @RequestParam(value = "cursor", required = false) Long cursor,
        @ApiParam(name = "limits", value = "커서 기준으로 limits개 검색. 필수") @RequestParam(value = "limits") Integer limits) {
        return new ResponseEntity<>(scrapService.getScrapList(searchBy, cursor, limits), HttpStatus.OK);
    }

    @PostMapping
    @Auth
    @ApiOperation(value = "스크랩 등록", notes = "스크랩을 등록합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<ScrapResponseDTO> createScrap(@RequestParam(value = "noticeId") Long noticeId) {
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
