package com.bcsdlab.biseo.dto.notice;

import java.sql.Timestamp;
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
public class NoticeResponseDTO {

    public Long id;
    public String title;
    public String content;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public List<String> files = new ArrayList<>();
    public List<String> pictures = new ArrayList<>();

    public void addFile(String file) {
        files.add(file);
    }

    public void addPicture(String picture) {
        pictures.add(picture);
    }
}
