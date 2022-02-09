package com.study.boardservice.service;

import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.exception.DuplicateValueException;
import com.study.boardservice.exception.InvalidValueException;
import com.study.boardservice.exception.MissingEssentialValueException;
import com.study.boardservice.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberDto signup(MemberDto request){
        request.validateSignupRequestDto();
        if(memberRepository.existsByEmail(request.getEmail()))
            throw new DuplicateValueException("Email is duplicated");

        Member member = request.toEntity();
        member.encodePassword();
        memberRepository.save(member);
        return MemberDto.builder().email(member.getEmail()).build();
    }

    @Transactional
    public MemberDto login(MemberDto request) {
        request.validateLoginRequestDto();
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new InvalidValueException("Email is invalid"));

        if(!member.matchPassword(request.getPassword()))
            throw new InvalidValueException("Password is invalid");

        return MemberDto.builder().email(member.getEmail()).name(member.getName()).build();
    }

    @Transactional
    public MemberDto findIdByName(String name) {
        if (name.equals(""))
            throw new MissingEssentialValueException("Name is empty");

        String id = memberRepository.findByName(name).orElseThrow(() ->
                new InvalidValueException("Name is invalid")).getId();
        return MemberDto.builder().id(id).build();
    }

    @Transactional
    public MemberDto findEmail(String id) {
        if (id.equals(""))
            throw new MissingEssentialValueException("Id is empty");

        String email = memberRepository.findById(id).orElseThrow(() ->
                new InvalidValueException("Id is invalid")).getEmail();
        return MemberDto.builder().email(email).build();
    }

    @Transactional
    public MemberDto findIdByEmailAndName(String email, String name) {
        if (email.equals(""))
            throw new MissingEssentialValueException("Email is empty");
        if (name.equals(""))
            throw new MissingEssentialValueException("Name is empty");

        String id = memberRepository.findByEmailAndName(email, name).orElseThrow(() ->
                new InvalidValueException("Account is not exist")).getId();
        return MemberDto.builder().id(id).build();
    }

    @Transactional
    public MemberDto modifyPassword(String id, MemberDto request){
        if (id.equals(""))
            throw new MissingEssentialValueException("Id is empty");

        request.validateModifyPasswordRequestDto();
        request.matchPasswords();
        Member member = memberRepository.findById(id).orElseThrow(() ->
                new InvalidValueException("Account is not exist"));
        member.modifyPassword(request.getNewPassword());
        member.encodePassword();
        return MemberDto.builder().email(member.getEmail()).build();
    }

    @Transactional
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new InvalidValueException("This account is not exist"));
    }
}
