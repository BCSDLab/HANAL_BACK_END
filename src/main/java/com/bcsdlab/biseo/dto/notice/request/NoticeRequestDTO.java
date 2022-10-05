package com.bcsdlab.biseo.dto.notice.request;

import com.bcsdlab.biseo.enums.Department;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDTO {
    @ApiModelProperty(value = "제목", required = true)
    private String title;
    @ApiModelProperty(value = "내용", required = true)
    private String content;
    @ApiModelProperty(value = "학과", required = true)
    private Department department;
    @ApiModelProperty(value = "학년", required = true)
    private List<Integer> grade = new ArrayList<>();
    @ApiModelProperty(value = "파일")
    List<MultipartFile> files = new ArrayList<>();
}
