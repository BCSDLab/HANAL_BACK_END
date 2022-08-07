package com.bcsdlab.biseo.dto.user;

import javax.validation.constraints.Email;
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
public class UserRequest {

    // TODO : 추가적인 validation 필요
    @NotNull(message = "이름이 비어있으면 안됩니다.")
    private String name;
    @Email(message = "이메일 형식의 데이터가 전송되어야 합니다.")
    @NotNull(message = "이메일은 비어있으면 안됩니다.")
    private String email;
    @NotNull(message = "비밀번호가 비어있으면 안됩니다.")
    private String password;
    @NotNull(message = "학번이 비어있으면 안됩니다.")
    private String student_id;
    @NotNull(message = "학과가 비어있으면 안됩니다.")
    private String department;
    @NotNull(message = "학년이 비어있으면 안됩니다.")
    private Integer grade;
}
