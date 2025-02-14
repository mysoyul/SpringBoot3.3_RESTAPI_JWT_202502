package com.boot3.myrestapi.security.userinfo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
//@AuthenticationPrincipal 어노테이션은 Authentication에 저장된 UserDetail 객체를 추출
//UserInfoUserDetails 객체에 저장된 UserInfo 객체를 추출하는 어노테이션
//Authentication => UserInfoUserDetails => UserInfo
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : userInfo")
public @interface CurrentUser {
}