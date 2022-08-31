package com.bcsdlab.biseo.dto.notice;

import com.bcsdlab.biseo.enums.FileType;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileModel {
    private Long id;
    private Long noticeId;
    private String path;
    private FileType type;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
