package com.study.boardservice.web.controller;

import com.study.boardservice.config.annotation.LoginMember;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.exception.InvalidValueException;
import com.study.boardservice.exception.MissingEssentialValueException;
import com.study.boardservice.exception.UnexpectedAccessException;
import com.study.boardservice.service.MemberService;
import com.study.boardservice.service.PostService;
import com.study.boardservice.web.dto.PostDto;
import com.study.boardservice.web.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@RestController
public class PostApiController {
    private final PostService postService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<?> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.findAll());
    }

    @GetMapping(params = {"type", "keyword"})
    public ResponseEntity<?> search(String type, String keyword){
        List<PostDto> response = new ArrayList<>();
        if (type.equals("title"))
            response = postService.findAllDescByTitle(keyword);
        else if (type.equals("author"))
            response = postService.findAllDescByAuthor(keyword);
        else
            throw new InvalidValueException("Search Type is invalid");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> create(@LoginMember SessionMember member, @RequestBody PostDto request) {
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(author, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Post post = postService.findById(id);
        PostDto response = new PostDto(post);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modify(@LoginMember SessionMember member, @PathVariable Long id, @RequestBody PostDto request){
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(postService.modify(id, author, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@LoginMember SessionMember member, @PathVariable Long id){
        if (member == null)
            throw new UnexpectedAccessException("로그인이 필요한 서비스입니다.");

        Member author = memberService.findByEmail(member.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(postService.remove(id, author));
    }
}
