package com.bcsdlab.biseo.dto.user.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    @NotBlank(message = "아이디가 비어있으면 안됩니다.")
    @ApiModelProperty(value = "아이디")
    private String accountId;
    @NotBlank(message = "인증번호가 비어있으면 안됩니다.")
    @ApiModelProperty(value = "인증번호", example = "1234")
    private String authCode;
}
