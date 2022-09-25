package com.bcsdlab.biseo.dto.notice;

import com.bcsdlab.biseo.enums.Department;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "제목")
    private String title;
    @ApiModelProperty(value = "내용")
    private String content;
    @ApiModelProperty(value = "학과")
    private Department department;
    @ApiModelProperty(value = "학년")
    private List<Integer> grade = new ArrayList<>();
}
