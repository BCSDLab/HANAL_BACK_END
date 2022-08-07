package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.user.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {

    UserModel findByAccountId(String account_id);
    UserModel findById(Long id);
    void signUp(UserModel user);
}
