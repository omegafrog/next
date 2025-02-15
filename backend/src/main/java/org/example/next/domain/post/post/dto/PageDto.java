package org.example.next.domain.post.post.dto;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.next.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class PageDto {

    @NonNull
    private List<PostDto> items;
    private long totalElementSize;
    private int totalPageNum;
    private int currentPageNum;

    public PageDto(Page<Post> page) {
        this.items=page.getContent().stream().map(PostDto::new).toList();
        this.totalElementSize = page.getTotalElements();
        this.totalPageNum = page.getTotalPages();
        this.currentPageNum = page.getNumber();
        this.pageSize = page.getSize();
    }

    private int pageSize;
}
