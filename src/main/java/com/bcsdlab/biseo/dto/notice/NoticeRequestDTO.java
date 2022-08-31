package com.bcsdlab.biseo.dto.notice;

import com.bcsdlab.biseo.enums.Department;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDTO {
    private String title;
    private String content;
    private Department department;
    private List<Integer> grade = new ArrayList<>();
}
