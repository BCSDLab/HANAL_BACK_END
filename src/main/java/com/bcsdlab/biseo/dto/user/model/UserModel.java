package com.bcsdlab.biseo.dto.user.model;

import com.bcsdlab.biseo.enums.UserType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private Long id;
    private String name;
    private String accountId;
    private String password;
    private String studentId;
    private Integer department;
    private UserType userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isAuth;
    private Boolean isDeleted;
}
