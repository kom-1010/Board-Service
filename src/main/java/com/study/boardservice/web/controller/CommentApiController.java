package com.study.boardservice.web.controller;

import com.study.boardservice.config.annotation.LoginMember;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.exception.UnexpectedAccessException;
import com.study.boardservice.service.CommentService;
import com.study.boardservice.service.MemberService;
import com.study.boardservice.service.PostService;
import com.study.boardservice.web.dto.CommentDto;
import com.study.boardservice.web.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
@RestController
public class CommentApiController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> create(@LoginMember SessionMember member, @PathVariable Long postId, @RequestBody CommentDto request){
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        Post post = postService.findById(postId);
        commentService.create(author, post, request);
        Map<String, Long> response = new HashMap<>();
        response.put("id", postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> findAllByPostId(@PathVariable Long postId){
        Post post = postService.findById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findAllByPostId(post));
    }

}
