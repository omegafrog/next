package org.example.next.domain.member.member.dto;

import lombok.Getter;
import org.example.next.domain.member.member.entity.Member;

import java.time.LocalDateTime;

@Getter
public class MemberDto {

    private long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String nickname;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.createdDate = member.getCreatedDate();
        this.modifiedDate = member.getModifiedDate();

        this.nickname = member.getNickname();
    }
}
