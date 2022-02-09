package com.study.boardservice.service;

import com.study.boardservice.domain.comment.Comment;
import com.study.boardservice.domain.comment.CommentRepository;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.web.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public void create(Member author, Post post, CommentDto request){
        request.validateCommentSaveRequestDto();
        Comment comment = Comment.builder().content(request.getContent()).author(author).post(post).build();
        commentRepository.save(comment);
        post.addComment(comment);
    }

    @Transactional
    public List<CommentDto> findAllByPostId(Post post) {
        return commentRepository.findAllByPostIdDesc(post.getId()).stream().map(CommentDto::new).collect(Collectors.toList());
    }
}
