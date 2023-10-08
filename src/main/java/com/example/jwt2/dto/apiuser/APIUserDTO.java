package com.example.jwt2.dto.apiuser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class APIUserDTO extends User {

    private String mid;
    private String mpw;
    private String nickname;
    private String role;

//    public APIUserDTO(String username, String password, Collection<? extends GrantedAuthority> authorities, String nickname) {
//        super(username, password, authorities);
//        this.mid = username;
//        this.mpw = password;
//        this.nickname = nickname;
//    }

    public APIUserDTO(String username, String password, String role, String nickname) {
        super(username, password, List.of(new SimpleGrantedAuthority(role)));
        this.mid = username;
        this.mpw = password;
        this.nickname = nickname;
        this.role = role;
    }
}
