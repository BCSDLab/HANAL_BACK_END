package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.user.UserCertifiedModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {

    UserModel findByAccountId(String accountId);
    UserModel findById(Long id);
    void signUp(UserModel user);
    void addAuthNum(UserCertifiedModel userCertifiedModel);
    UserCertifiedModel findRecentAuthNumByUserAccountId(String accountId);
    void deleteAuthNumById(Long id);
    void setUserAuth(Long id);
    void updateDepartment(UserModel user);
    void updatePassword(UserModel user);

}
