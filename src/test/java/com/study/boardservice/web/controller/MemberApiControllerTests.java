package com.study.boardservice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.web.dto.MemberDto;
import com.study.boardservice.web.dto.SessionMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberApiControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;

    private String email = "abc12345@abcde.com";
    private String password = "abc12345";
    private String name = "Teddy";
    private String phone = "00000000000";
    private String encodedPassword;

    @BeforeEach
    public void setUp(){
        encodedPassword = new BCryptPasswordEncoder().encode(password);
    }

    @AfterEach
    public void tearDown(){
        memberRepository.deleteAll();
    }

    @Test
    public void 회원가입() throws Exception {
        // given
        MemberDto request = MemberDto.builder().email(email).password(password).name(name).phone(phone).build();
        String url = "/api/v1/member/signup";

        // when
        mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());

        // then
        Member member = memberRepository.findAll().get(0);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isNotEqualTo(password);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getPhone()).isEqualTo(phone);
    }

    @Test
    public void 필수값_누락에_의한_회원가입_실패() throws Exception {
        // given
        MemberDto request = MemberDto.builder().email(null).password(password).name(name).phone(phone).build();
        String url = "/api/v1/member/signup";

        // when
        ResultActions actions = mvc.perform(post(url)
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 이메일_중복에_의한_회원가입_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build());
        MemberDto request = MemberDto.builder().email(email).password(password).name(name).phone(phone).build();
        String url = "/api/v1/member/signup";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build());
        MemberDto request = MemberDto.builder().email(email).password(password).build();
        String url = "/api/v1/member/login";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    public void 필수값_누락에_의한_로그인_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build());
        MemberDto request = MemberDto.builder().email(null).password(password).build();
        String url = "/api/v1/member/login";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지는_않는_계정으로_인한_로그인_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build());
        MemberDto request = MemberDto.builder().email("invalid_email").password(password).build();
        String url = "/api/v1/member/login";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 잘못된_비밀번호에_의한_로그인_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build());
        MemberDto request = MemberDto.builder().email(email).password("invalid password").build();
        String url = "/api/v1/member/login";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 이름으로_memberId_찾기() throws Exception {
        // given
        String id = memberRepository.save(
                Member.builder().email(email).password(password).name(name).phone(phone).build()).getId();
        String url = "/api/v1/member/id?name=" + name;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

    }

    @Test
    public void 필수값_누락에_의한_이름으로_memberId_찾기_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build());
        String url = "/api/v1/member/id?name=";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_계정으로_인한_이름으로_memberId_찾기_실패() throws Exception {
        // given
        String url = "/api/v1/member/id?name=" + name;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 이메일찾기() throws Exception {
        // given
        String id = memberRepository.save(
                Member.builder().email(email).password(password).name(name).phone(phone).build()).getId();
        String url = "/api/v1/member/email?id=" + id;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    public void 필수값_누락으로_인한_이메일찾기_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build());
        String url = "/api/v1/member/email?id=";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_계정으로_인한_이메일찾기_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build());
        String id = "invalid id";
        String url = "/api/v1/member/email?id=" + id;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 이메일과_이름으로_memberId_찾기() throws Exception {
        // given
        String id = memberRepository.save(
                Member.builder().email(email).password(password).name(name).phone(phone).build()).getId();
        String url = "/api/v1/member/id?email=" + email + "&name=" + name;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

    }

    @Test
    public void 필수값_누락에_의한_이메일과_이름으로_memberId_찾기_실패() throws Exception {
        // given
        memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build());
        String url = "/api/v1/member/id?email=&name=" + name;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_계정으로_인한_이메일과_이름으로_memberId_찾기_실패() throws Exception {
        // given
        String url = "/api/v1/member/id?email=" + email + "&name=" + name;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비밀번호변경() throws Exception {
        // given
        String id = memberRepository.save(
                Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build()).getId();
        String newPassword = "99999";
        MemberDto request = MemberDto.builder().newPassword(newPassword).checkPassword(newPassword).build();
        String url = "/api/v1/member/password?id=" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        Member member = memberRepository.findAll().get(0);
        assertThat(member.matchPassword(newPassword)).isTrue();
    }

    @Test
    public void 필수값_누락에_의한_비밀변호변경_실패() throws Exception {
        // given
        String id = memberRepository.save(
                Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build()).getId();
        String newPassword = "99999";
        MemberDto request = MemberDto.builder().newPassword(newPassword).build();
        String url = "/api/v1/member/password?id=" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_계정에_의한_비밀번호변경_실패() throws Exception {
        // given
        String id = "invalid id";
        String newPassword = "99999";
        MemberDto request = MemberDto.builder().newPassword(newPassword).checkPassword(newPassword).build();
        String url = "/api/v1/member/password?id=" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비밀번호_불일치에_의한_비밀번호변경_실패() throws Exception {
        // given
        String id = memberRepository.save(
                Member.builder().email(email).password(encodedPassword).name(name).phone(phone).build()).getId();
        String newPassword = "99999";
        MemberDto request = MemberDto.builder().newPassword(newPassword).checkPassword("invalid password").build();
        String url = "/api/v1/member/password?id=" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }
}
