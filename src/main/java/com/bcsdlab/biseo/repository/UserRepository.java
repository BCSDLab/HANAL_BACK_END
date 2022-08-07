package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.user.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {

    String findByEmail(String email);
    UserModel findById(Long id);
    void signUp(UserModel user);
}
