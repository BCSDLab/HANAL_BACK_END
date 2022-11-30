package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.user.model.UserAuthModel;
import com.bcsdlab.biseo.dto.user.model.UserModel;
import com.bcsdlab.biseo.enums.AuthType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {

    UserModel findByAccountId(String accountId);

    UserModel findById(Long id);

    void signUp(UserModel user);

    void addAuthNum(UserAuthModel userAuthModel);

    UserAuthModel findRecentAuthNumByUserAccountId(String accountId, AuthType authType);

    void deleteAuthNumById(Long id);

    void setUserAuth(Long id);

    void updateDepartment(UserModel user);

    void updatePassword(UserModel user);

    Integer findUserDepartmentById(Long id);

}
