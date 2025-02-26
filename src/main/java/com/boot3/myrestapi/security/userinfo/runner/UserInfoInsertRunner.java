package com.boot3.myrestapi.security.userinfo.runner;

import com.boot3.myrestapi.security.userinfo.UserInfo;
import com.boot3.myrestapi.security.userinfo.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod")
public class UserInfoInsertRunner implements ApplicationRunner {
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("adminboot");
        userInfo.setPassword(
                passwordEncoder.encode("pwd1"));
        userInfo.setEmail("admin@aa.com");
        userInfo.setRoles("ROLE_ADMIN,ROLE_USER");

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setName("userboot");
        userInfo2.setPassword(
                passwordEncoder.encode("pwd2"));
        userInfo2.setEmail("user@aa.com");
        userInfo2.setRoles("ROLE_USER");

        userInfoRepository.saveAll(List.of(userInfo, userInfo2));

    }
}