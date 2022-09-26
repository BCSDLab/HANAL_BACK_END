package com.bcsdlab.biseo.dto.user.request;

import com.bcsdlab.biseo.annotation.Enum;
import com.bcsdlab.biseo.enums.Department;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDepartmentDTO {
    @NotBlank(message = "학과가 비어있으면 안됩니다.")
    @Enum(enumClass = Department.class) // 예외 시 MethodArgumentNotValidException 반환
    @ApiModelProperty(value = "학과")
    private String department;

    @NotNull(message = "학년이 비어있으면 안됩니다.")
    @Min(value = 1, message = "학년은 1학년부터 4학년까지 선택할 수 있습니다.")
    @Max(value = 4, message = "학년은 1학년부터 4학년까지 선택할 수 있습니다.")
    @ApiModelProperty(value = "학년(1 ~ 4)")
    private Integer grade;
}
