package com.bcsdlab.biseo.dto.notice.response;

import com.bcsdlab.biseo.dto.notice.model.NoticeFileModel;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoDto {

    private String path;
    private String savedName;

    public FileInfoDto(NoticeFileModel noticeFileModel) {
        this.path = noticeFileModel.getPath();
        this.savedName = noticeFileModel.getSavedName();
    }
}
