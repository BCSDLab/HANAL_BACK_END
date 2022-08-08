package com.bcsdlab.biseo.dto.user;

import com.bcsdlab.biseo.annotation.ValidationGroups;
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
    @NotNull(groups = {ValidationGroups.SignUp.class}, message = "이름이 비어있으면 안됩니다.")
    private String name;
    @NotNull(groups = {ValidationGroups.SignUp.class, ValidationGroups.Login.class}, message = "이메일은 비어있으면 안됩니다.")
    private String accountId;
    @NotNull(groups = {ValidationGroups.SignUp.class, ValidationGroups.Login.class}, message = "비밀번호가 비어있으면 안됩니다.")
    private String password;
    @NotNull(groups = {ValidationGroups.SignUp.class}, message = "학번이 비어있으면 안됩니다.")
    private String studentId;
    @NotNull(groups = {ValidationGroups.SignUp.class, ValidationGroups.ChangeDepartment.class}, message = "학과가 비어있으면 안됩니다.")
    private String department;
    @NotNull(groups = {ValidationGroups.SignUp.class, ValidationGroups.ChangeDepartment.class}, message = "학년이 비어있으면 안됩니다.")
    private Integer grade;
}
