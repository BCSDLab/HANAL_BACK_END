package com.bcsdlab.biseo.dto.user;

import com.bcsdlab.biseo.annotation.ValidationGroups;
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
public class UserRequestDTO {

    // TODO : 추가적인 validation 필요
    @NotBlank(groups = {ValidationGroups.SignUp.class}, message = "이름이 비어있으면 안됩니다.")
    private String name;
    @NotBlank(groups = {ValidationGroups.SignUp.class, ValidationGroups.Login.class, ValidationGroups.Mail.class}, message = "아이디가 비어있으면 안됩니다.")
    private String accountId;
    @NotBlank(groups = {ValidationGroups.SignUp.class, ValidationGroups.Login.class}, message = "비밀번호가 비어있으면 안됩니다.")
    @Pattern(groups = {ValidationGroups.SignUp.class}, regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "비밀번호는 숫자와 영어, 특수문자로 이루어져 있어야 합니다.")
    private String password;
    @NotBlank(groups = {ValidationGroups.SignUp.class}, message = "학번이 비어있으면 안됩니다.")
    private String studentId;
    @NotBlank(groups = {ValidationGroups.SignUp.class, ValidationGroups.ChangeDepartment.class}, message = "학과가 비어있으면 안됩니다.")
    private String department;
    @NotNull(groups = {ValidationGroups.SignUp.class, ValidationGroups.ChangeDepartment.class}, message = "학년이 비어있으면 안됩니다.")
    private Integer grade;
}
