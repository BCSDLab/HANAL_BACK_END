package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.notice.model.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeFileModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeReadModel;
import com.bcsdlab.biseo.dto.notice.response.NoticeListResponseDTO;
import com.bcsdlab.biseo.dto.notice.response.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.notice.model.NoticeTargetModel;
import com.bcsdlab.biseo.dto.user.model.UserModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeRepository {

    void createNotice(NoticeModel noticeModel);
    void createTarget(List<NoticeTargetModel> targetModel);
    List<NoticeListResponseDTO> getNoticeList(Integer department, Long userId, String searchBy, Long cursor, Integer limits);
    NoticeAndFileModel findNoticeAndFileById(Long noticeId, Long userId);
    NoticeReadModel findReadLogByUserId(Long noticeId, Long userId);
    void createReadLog(NoticeReadModel noticeReadModel);
    List<UserModel> findReadLogByNoticeId(Long noticeId);
    List<UserModel> findNotReadLogByNoticeId(Long noticeId);
    List<Integer> findTargetByNoticeId(Long noticeId);
    NoticeModel findNoticeById(Long noticeId);
    void updateNoticeById(NoticeModel notice);
    void deleteTargetByNoticeId(Long noticeId);
    void deleteReadListByNoticeId(Long noticeId);

    void deleteNoticeById(Long noticeId);
    void createFiles(List<NoticeFileModel> files);
    void deleteNoticeFileByNoticeId(Long noticeId);
}
