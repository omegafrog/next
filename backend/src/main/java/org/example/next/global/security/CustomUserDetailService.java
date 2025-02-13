package org.example.next.global.security;

import lombok.RequiredArgsConstructor;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.member.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new SecurityUser(member.getId(), member.getUsername(), member.getPassword(), member.getAuthorities());
    }
}
