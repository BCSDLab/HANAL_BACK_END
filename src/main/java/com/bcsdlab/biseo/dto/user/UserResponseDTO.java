package com.bcsdlab.biseo.dto.user;

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
public class UserResponseDTO {

    private String name;
    private String accountId;
    private String studentId;
    private String department;
    private Integer grade;
    private Boolean isAuth;
}
