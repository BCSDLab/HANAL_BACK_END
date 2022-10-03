package com.bcsdlab.biseo.dto.user.request;

import com.bcsdlab.biseo.annotation.Enum;
import com.bcsdlab.biseo.enums.Department;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class UserSignUpDTO {

    // TODO : 추가적인 validation 필요
    @NotBlank(message = "이름이 비어있으면 안됩니다.")
    @ApiModelProperty(value = "이름")
    private String name;

    @NotBlank(message = "아이디가 비어있으면 안됩니다.")
    @ApiModelProperty(value = "아이디")
    private String accountId;

    @NotBlank(message = "비밀번호가 비어있으면 안됩니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "비밀번호는 숫자와 영어, 특수문자로 이루어져 있어야 합니다.")
    @ApiModelProperty(value = "비밀번호")
    private String password;

    @NotBlank(message = "학번이 비어있으면 안됩니다.")
    @ApiModelProperty(value = "학번")
    private String studentId;

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
