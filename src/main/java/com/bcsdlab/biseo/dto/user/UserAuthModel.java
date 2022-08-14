package com.bcsdlab.biseo.dto.user;

import java.sql.Timestamp;
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
    private Timestamp createdAt;
    private boolean isDeleted;
}
