package com.study.boardservice.web.dto;

import com.study.boardservice.domain.comment.Comment;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.exception.MissingEssentialValueException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CommentDto {
    private Long id;
    private String content;
    private String authorName;
    private Long postId;
    private String createdAt;
    private String modifiedAt;

    @Builder
    public CommentDto(Long id, String content, Member author, Post post, LocalDateTime createdAt, LocalDateTime modifiedAt){
        this.id = id;
        this.content = content;
        this.authorName = author.getName();
        this.postId = post.getId();
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.modifiedAt = modifiedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public CommentDto(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorName = comment.getAuthor().getName();
        this.postId = comment.getPost().getId();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.modifiedAt = comment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void validateCommentSaveRequestDto() {
        if (content == null || content.equals(""))
            throw new MissingEssentialValueException("내용이 없습니다.");
    }
}
