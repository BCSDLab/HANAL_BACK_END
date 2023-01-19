package com.bcsdlab.biseo.dto.user.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtDTO {
    String accessToken;
    String refreshToken;

    public JwtDTO(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }
}
