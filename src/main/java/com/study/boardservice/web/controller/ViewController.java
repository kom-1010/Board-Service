package com.study.boardservice.web.controller;

import com.study.boardservice.config.annotation.LoginMember;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.service.MemberService;
import com.study.boardservice.service.PostService;
import com.study.boardservice.service.RecommendService;
import com.study.boardservice.web.dto.PostDto;
import com.study.boardservice.web.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ViewController {
    private final MemberService memberService;
    private final PostService postService;
    private final RecommendService recommendService;
    private final HttpSession session;

    @GetMapping("/")
    public String index(@LoginMember SessionMember member, Model model) {
        if (member != null)
            model.addAttribute("member", member);

        List<PostDto> response = postService.findAll();
        model.addAttribute("posts", response);
        return "index";
    }

    @GetMapping(value = "/board", params = {"type", "keyword"})
    public String search(@LoginMember SessionMember member, String type, String keyword, Model model){
        if (member != null)
            model.addAttribute("member", member);

        List<PostDto> response = new ArrayList<>();
        if (type.equals("title"))
            response = postService.findAllDescByTitle(keyword);
        else if (type.equals("author"))
            response = postService.findAllDescByAuthor(keyword);
        else
            response = postService.findAll();

        model.addAttribute("posts", response);
        return "index";
    }

    @GetMapping("/posts/{id}")
    public String post(@LoginMember SessionMember member, @PathVariable Long id, Model model){
        if (member != null)
            model.addAttribute("member", member);

        Member author = memberService.findByEmail(member.getEmail());
        Post post = postService.findById(id);
        model.addAttribute("post", new PostDto(post));
        model.addAttribute("isRecommended", recommendService.exist(author, post));
        return "post";
    }

    @GetMapping("/posts/{id}/update")
    public String update(@LoginMember SessionMember member, @PathVariable Long id, Model model){
        if (member == null)
            return "redirect:/login";

        Member author = memberService.findByEmail(member.getEmail());
        if (!postService.checkAuthor(id, author))
            return "redirect:/";

        model.addAttribute("member", member);
        model.addAttribute("post", postService.findById(id));
        return "update";
    }

    @GetMapping("/signup")
    public String signup(@LoginMember SessionMember member, Model model){
        if (member != null)
            return "redirect:/";

        return "signup";
    }

    @GetMapping("/login")
    public String login(@LoginMember SessionMember member, Model model){
        if (member != null)
            return "redirect:/";

        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("member");
        return "redirect:/";
    }

    @GetMapping("/find-email")
    public String findEmail(@LoginMember SessionMember member, Model model) {
        if (member != null)
            return "redirect:/";

        return "find-email"; }

    @GetMapping(value = "/get-email", params = {"id"})
    public String getEmail(@LoginMember SessionMember member, @RequestParam String id, Model model) {
        if (member != null)
            return "redirect:/";

        if (id.equals(""))
            return "redirect:/login";

        String email = memberService.findEmail(id).getEmail();
        model.addAttribute("email", email);
        return "get-email";
    }

    @GetMapping("/find-password")
    public String findPassword(@LoginMember SessionMember member, Model model) {
        if (member != null)
            return "redirect:/";

        return "find-password";
    }

    @GetMapping(value = "/change-password", params = {"id"})
    public String changePassword(@LoginMember SessionMember member, @RequestParam String id, Model model) {
        if (member != null)
            return "redirect:/";

        if (id.equals(""))
            return "redirect:/login";

        model.addAttribute("id", id);
        return "change-password";
    }

    @GetMapping("/create")
    public String create(@LoginMember SessionMember member, Model model){
        if (member == null)
            return "redirect:/login";
        else{
            model.addAttribute("member", member);
            return "create";
        }
    }
}
