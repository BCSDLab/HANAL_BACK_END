package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.user.UserCertifiedModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {

    UserModel findByAccountId(String account_id);
    UserModel findById(Long id);
    void signUp(UserModel user);
    void addAuthNum(UserCertifiedModel userCertifiedModel);
    UserCertifiedModel findRecentAuthNumByUserId(Long user_id);
    void deleteAuthNumById(Long id);
    void setUserAuth(Long id);
    void updateDepartment(UserModel user);

}
