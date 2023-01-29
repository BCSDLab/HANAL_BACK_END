package com.bcsdlab.biseo.dto.user.model;

import com.bcsdlab.biseo.enums.AuthType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthModel {
    private Long id;
    private Long userId;
    private String authNum;
    private LocalDateTime createdAt;
    private AuthType authType;
    private boolean isDeleted;
}
