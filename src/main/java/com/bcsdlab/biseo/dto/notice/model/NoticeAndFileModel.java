package com.bcsdlab.biseo.dto.notice.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeAndFileModel {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long scrapId;
    private List<NoticeFileModel> files = new ArrayList<>();
}
