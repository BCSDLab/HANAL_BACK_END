package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import com.bcsdlab.biseo.enums.UserType;
import com.bcsdlab.biseo.service.NoticeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Auth(type = UserType.COUNCIL)
    @ApiOperation(value = "공지 작성", notes = "공지를 작성합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<Long> createNotice(@ModelAttribute NoticeRequestDTO request) {
        return new ResponseEntity<>(noticeService.createNotice(request), HttpStatus.OK);
    }

    @GetMapping
    @Auth
    @ApiOperation(value = "공지 목록 조회", notes = "공지 목록을 조회합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<List<NoticeResponseDTO>> getNoticeList(
        @ApiParam(name = "searchBy", value = "제목 검색") @RequestParam(value = "searchBy", required = false) String searchBy,
        @ApiParam(name = "cursor", value = "커서 기반 페이지네이션 사용") @RequestParam(value = "cursor", required = false) Long cursor,
        @ApiParam(name = "limits", value = "커서 기준으로 limits개 검색") @RequestParam(value = "limits", required = false) Integer limits) {
        return new ResponseEntity<>(noticeService.getNoticeList(searchBy, cursor, limits),
            HttpStatus.OK);
    }

    @GetMapping("/{noticeId}")
    @Auth
    @ApiOperation(value = "공지 조회", notes = "공지를 조회합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<NoticeResponseDTO> getNotice(
        @ApiParam(name = "noticeId", value = "noticeId번 공지 조회") @PathVariable("noticeId") Long noticeId) {
        return new ResponseEntity<>(noticeService.getNotice(noticeId), HttpStatus.OK);
    }

    @GetMapping("/{noticeId}/read-list")
    @Auth(type = UserType.COUNCIL)
    @ApiOperation(value = "공지를 읽은 사람 조회", notes = "공지를 읽은 사람과 읽지 않은 사람을 각각 조회합니다..", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<List<UserResponseDTO>> getReadLog(
        @ApiParam(name = "noticeId", value = "noticeId번 공지 조회") @PathVariable("noticeId") Long noticeId,
        @ApiParam(name = "isRead", value = "true : 읽은사람 \n false : 읽지 않은 사람") @RequestParam("isRead") Boolean isRead) {
        return new ResponseEntity<>(noticeService.getReadLog(noticeId, isRead), HttpStatus.OK);
    }


    @PostMapping(value = "/{noticeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Auth(type = UserType.COUNCIL)
    @ApiOperation(value = "공지 수정", notes = "공지를 수정합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<Long> updateNotice(@PathVariable("noticeId") Long noticeId, @ModelAttribute NoticeRequestDTO request) {
        return new ResponseEntity<>(noticeService.updateNotice(noticeId, request),
            HttpStatus.OK);
    }

    @DeleteMapping("/{noticeId}")
    @Auth(type = UserType.COUNCIL)
    @ApiOperation(value = "공지 삭제", notes = "공지를 삭제합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<String> deleteNotice(
        @ApiParam(name = "noticeId", value = "noticeId번의 공지를 삭제합니다.") @PathVariable("noticeId") Long noticeId) {
        return new ResponseEntity<>(noticeService.deleteNotice(noticeId), HttpStatus.OK);
    }
}
