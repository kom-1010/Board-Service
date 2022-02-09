package com.study.boardservice.web.controller;

import com.study.boardservice.exception.MissingEssentialValueException;
import com.study.boardservice.service.MemberService;
import com.study.boardservice.web.dto.MemberDto;
import com.study.boardservice.web.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberApiController {
    private final MemberService memberService;
    private final HttpSession session;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDto request){
        MemberDto response = memberService.login(request);
        session.setAttribute("member", new SessionMember(response.getEmail(), response.getName()));
        return ResponseEntity.status(HttpStatus.OK).body(MemberDto.builder().email(response.getEmail()).build());
    }

    @GetMapping(value = "/id", params = {"name"})
    public ResponseEntity<?> findIdByName(@RequestParam String name){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.findIdByName(name));
    }

    @GetMapping(value = "/email", params = {"id"})
    public ResponseEntity<?> findEmail(@RequestParam String id){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.findEmail(id));
    }

    @GetMapping(value = "/id", params = {"email", "name"})
    public ResponseEntity<?> findIdByEmailAndName(@RequestParam String email, String name){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.findIdByEmailAndName(email, name));
    }

    @PutMapping(value = "/password", params = {"id"})
    public ResponseEntity<?> modifyPassword(@RequestParam String id, @RequestBody MemberDto request){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.modifyPassword(id, request));
    }
}
