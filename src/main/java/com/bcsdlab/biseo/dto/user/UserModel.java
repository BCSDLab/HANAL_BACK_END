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
    private String email;
    private String password;
    private String student_id;
    private int department;
    private UserType user_type;
    private Timestamp created_at;
    private Timestamp updated_at;
    private boolean is_auth;
    private boolean is_deleted;
}
