package org.example.next.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private Long id;
    public SecurityUser(Long id, String username,String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, "", authorities);
        this.id = id;
    }
}
