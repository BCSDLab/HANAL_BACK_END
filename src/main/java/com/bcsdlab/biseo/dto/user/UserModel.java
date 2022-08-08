package com.bcsdlab.biseo.dto.user;

import com.bcsdlab.biseo.enums.UserType;
import java.sql.Timestamp;
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
public class UserModel {

    private Long id;
    private String name;
    private String accountId;
    private String password;
    private String studentId;
    private Integer department;
    private UserType userType;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isAuth;
    private Boolean isDeleted;
}
