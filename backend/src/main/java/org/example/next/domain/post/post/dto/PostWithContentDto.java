package org.example.next.domain.post.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.example.next.domain.post.post.entity.Post;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class PostWithContentDto {

    @NonNull
    private long id;
    @NonNull
    @JsonProperty("createdDatetime")
    private LocalDateTime createdDate;
    @NonNull
    @JsonProperty("modifiedDatetime")
    private LocalDateTime modifiedDate;
    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private long authorId;
    @NonNull
    private String authorName;
    @NonNull
    private boolean opened;
    @NonNull
    private boolean listed;

    public PostWithContentDto(Post post) {
        this.id = post.getId();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorId = post.getAuthor().getId();
        this.authorName = post.getAuthor().getNickname();
        this.opened = post.isOpened();
        this.listed = post.isListed();
    }
}
