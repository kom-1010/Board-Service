package com.study.boardservice.domain.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MemberRepositoryTests {
    @Autowired
    MemberRepository memberRepository;

    private String email = "abc12345@abcde.com";
    private String password = "abc12345";
    private String name = "Teddy";
    private String phone = "00000000000";

    @AfterEach
    public void tearDown(){
        memberRepository.deleteAll();
    }


    @Test
    public void 생성하기(){
        // given
        Member member = Member.builder().email(email).password(password).name(name).phone(phone).build();

        // when
        memberRepository.save(member);

        // then
        Member savedMember = memberRepository.findAll().get(0);
        assertThat(savedMember.getEmail()).isEqualTo(email);
        assertThat(savedMember.getPassword()).isEqualTo(password);
        assertThat(savedMember.getName()).isEqualTo(name);
        assertThat(savedMember.getPhone()).isEqualTo(phone);
    }

    @Test
    public void id로_조회하기(){
        // given
        String id = memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build()).getId();

        // when
        Member member = memberRepository.findById(id).get();

        // then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getPhone()).isEqualTo(phone);
    }

    @Test
    public void email로_조회하기(){
        // given
        memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build());

        // when
        Member member = memberRepository.findByEmail(email).get();

        // then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getPhone()).isEqualTo(phone);
    }

    @Test
    public void 수정하기(){
        // given
        Member member = Member.builder().email(email).password(password).name(name).phone(phone).build();
        memberRepository.save(member);

        String name2 = "Tom";
        String phone2 = "11111111111";
        String password2 = "def12345";
        member.modifyProfile(name2, phone2);
        member.modifyPassword(password2);

        // when
        memberRepository.save(member);

        // then
        Member modifiedMember = memberRepository.findAll().get(0);
        assertThat(modifiedMember.getPassword()).isEqualTo(password2);
        assertThat(modifiedMember.getName()).isEqualTo(name2);
        assertThat(modifiedMember.getPhone()).isEqualTo(phone2);
    }

    @Test
    public void 삭제하기(){
        // given
        String id = memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build()).getId();

        // when
        memberRepository.deleteById(id);

        // then
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }
}
