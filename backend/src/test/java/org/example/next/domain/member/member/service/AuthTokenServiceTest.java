package org.example.next.domain.member.member.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.member.member.repository.MemberRepository;
import org.example.next.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {

    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @Value(value = "${custom.jwt.secret-key}")
    private String keyString;


    @Test
    void test(){

        Claims claims = Jwts.claims()
                .add("name", "Paul")
                .add("age", 23)
                .build();
        String jwt = Util.Jwt.createToken(keyString, claims);
        System.out.println("jwt = " + jwt);

        assertThat(jwt).isNotNull();

        Map<String, Object> payload= Util.Jwt.getClaims(jwt, keyString);


        assertThat(payload.get("name")).isEqualTo("Paul");
        assertThat(payload.get("age")).isEqualTo(23);
    }

    @Test
    @DisplayName("access token 생성")
    void test2(){
        String username = "user1";
        Member member = memberRepository.findByUsername(username).get();
        String accessToken = authTokenService.genAccessToken(member);
        System.out.println("accessToken = " + accessToken);
    }

    @Test
    @DisplayName("jwt valid check")
    void checkValid(){

        Member user1 = memberService.findByUsername("user1").get();
        String jwt = authTokenService.genAccessToken(user1);

        boolean valid = Util.Jwt.checkValid(jwt,keyString );
        assertThat(valid).isTrue();

        Map<String, Object> claims1 = authTokenService.getPayload(jwt, keyString);
        assertThat(claims1).containsAllEntriesOf(Map.of("username", user1.getUsername(), "id", user1.getId()));
    }
}
