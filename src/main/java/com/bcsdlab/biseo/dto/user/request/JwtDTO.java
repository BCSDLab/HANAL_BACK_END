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
public class JwtDTO {
    @NotBlank(message = "access 토큰이 비어있으면 안됩니다.")
    @ApiModelProperty(value = "access 토큰")
    String access;

    @NotBlank(message = "refresh 토큰이 비어있으면 안됩니다.")
    @ApiModelProperty(value = "refresh 토큰")
    String refresh;
}
