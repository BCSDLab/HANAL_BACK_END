package com.bcsdlab.biseo.dto.user;

import com.bcsdlab.biseo.annotation.ValidationGroups;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordDTO {

    private String password;
    
    @Pattern(groups = {ValidationGroups.SignUp.class}, regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "비밀번호는 숫자와 영어, 특수문자로 이루어져 있어야 합니다.")
    private String newPassword;
}
