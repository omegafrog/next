package org.example.next.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private Long id;
    private String nickname;
    public SecurityUser(Long id, String username,String password, String nickname, Collection<? extends GrantedAuthority> authorities) {
        super(username, "", authorities);
        this.nickname = nickname;
        this.id = id;
    }
}
