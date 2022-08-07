package com.bcsdlab.biseo.dto.user;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.enums.AuthType;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertifiedModel {
    private Long id;
    private Long user_id;
    private String auth_num;
    private AuthType auth_type;
    private Timestamp created_at;
    private boolean is_deleted;
}
