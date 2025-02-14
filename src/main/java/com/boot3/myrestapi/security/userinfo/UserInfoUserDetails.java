package com.boot3.myrestapi.security.userinfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
    AuthenticationManager 가 인증처리 할때 getUsername() 과 getPassword() 사용
    Entity(테이블) 저장된 username와 password를 가져와서 저장해야 함
 */
public class UserInfoUserDetails implements UserDetails {
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private UserInfo userInfo;

    public UserInfoUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.email=userInfo.getEmail();
        this.password=userInfo.getPassword();
        //userInfo.getRoles() : ROLE_ADMIN,ROLE_USER
        this.authorities= Arrays.stream(userInfo.getRoles().split(",")) //Stream<String>
                // Stream<String> => Stream<SimpleGrantedAuthority>
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                //.map(SimpleGrantedAuthority::new)
                //Stream<SimpleGrantedAuthority> => List<SimpleGrantedAuthority>
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
    }    

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}