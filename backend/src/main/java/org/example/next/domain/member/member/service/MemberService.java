package org.example.next.domain.member.member.service;

import lombok.RequiredArgsConstructor;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.member.member.repository.MemberRepository;
import org.example.next.global.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    @Value(value = "${custom.jwt.secret-key}")
    private String secretKey;

    public Member join(String username, String password, String nickname) {

        Member member = Member.builder()
                .username(username)
                .password(password)
                .apiKey(username)
                .nickname(nickname)
                .build();

        return memberRepository.save(member);
    }

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public String getAuthToken(Member member){
        return authTokenService.genAccessToken(member);
    }

    public Member login(String username, String password) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException("401-1", "잘못된 아이디입니다."));

        if(!member.getPassword().equals(password)) {
           throw new ServiceException("401-2", "로그인 실패");
        }
        return member;
    }

    public Optional<Member> getMemberByAccessToken(String authToken) {
        Map<String, Object> payload = authTokenService.getPayload(authToken, secretKey);

        if(payload.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(Member.builder()
                .id((Long) payload.get("id"))
                .username((String) payload.get("username"))
                        .nickname((String) payload.get("nickname"))
                .build());
    }

    public String genAccessTokenOnly(Member member) {
        return authTokenService.genAccessToken(member);
    }
}