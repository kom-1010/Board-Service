package com.study.boardservice.web.controller;

import com.study.boardservice.config.annotation.LoginMember;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.exception.UnexpectedAccessException;
import com.study.boardservice.service.MemberService;
import com.study.boardservice.service.PostService;
import com.study.boardservice.service.RecommendService;
import com.study.boardservice.web.dto.PostDto;
import com.study.boardservice.web.dto.RecommendDto;
import com.study.boardservice.web.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class RecommendApiController {
    private final MemberService memberService;
    private final RecommendService recommendService;
    private final PostService postService;

    @PostMapping("/posts/{postId}/recommend")
    public ResponseEntity<?> recommend(@LoginMember SessionMember member, @PathVariable Long postId){
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        Post post = postService.findById(postId);
        recommendService.save(author, post);
        return ResponseEntity.status(HttpStatus.CREATED).body(RecommendDto.builder().postId(post.getId()).build());
    }

    @DeleteMapping("/posts/{postId}/recommend")
    public ResponseEntity<?> cancelRecommend(@LoginMember SessionMember member, @PathVariable Long postId){
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        Post post = postService.findById(postId);
        recommendService.remove(author, post);
        return ResponseEntity.status(HttpStatus.OK).body(RecommendDto.builder().postId(postId).build());
    }

    @GetMapping("/posts/{postId}/recommend")
    public ResponseEntity<?> existRecommend(@LoginMember SessionMember member, @PathVariable Long postId){
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        Post post = postService.findById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(PostDto.builder().isRecommended(recommendService.exist(author, post)).build());
    }
}
