package com.study.boardservice.web.dto;

import com.study.boardservice.domain.post.Post;
import com.study.boardservice.exception.MissingEssentialValueException;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private String modifiedAt;
    private int recommends;
    private boolean isRecommended;

    public PostDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor().getName();
        this.createdAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.modifiedAt = post.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.recommends = post.getRecommends().size();
    }

    @Builder
    public PostDto(Long id, String title, String content, String author, String createdAt, String modifiedAt, int recommends, boolean isRecommended){
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.recommends = recommends;
        this.isRecommended = isRecommended;
    }

    public void validatePostCreateRequestDto(){
        if (title == null || title.equals(""))
            throw new MissingEssentialValueException("Title is empty");
        if (content == null || content.equals(""))
            throw new MissingEssentialValueException("Content is empty");
    }
}
