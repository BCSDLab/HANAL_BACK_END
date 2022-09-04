package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.notice.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.NoticeModel;
import com.bcsdlab.biseo.dto.notice.NoticeTargetModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeRepository {

    void createNotice(NoticeModel noticeModel);
    void createTarget(NoticeTargetModel targetModel);
    NoticeAndFileModel findByNoticeId(Long id);
}
