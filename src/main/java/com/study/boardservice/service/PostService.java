package com.study.boardservice.service;

import com.study.boardservice.domain.like.Recommend;
import com.study.boardservice.domain.like.RecommendRepository;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import com.study.boardservice.exception.InvalidValueException;
import com.study.boardservice.exception.UnexpectedAccessException;
import com.study.boardservice.web.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final RecommendRepository recommendRepository;

    @Transactional
    public List<PostDto> findAll(){
        return postRepository.findAllDesc().stream().map(PostDto::new).collect(Collectors.toList());
    }

    @Transactional
    public PostDto create(Member author, PostDto request) {
        request.validatePostCreateRequestDto();
        Post post = Post.builder().title(request.getTitle()).content(request.getContent()).author(author).build();
        Long id = postRepository.save(post).getId();
        return PostDto.builder().id(id).build();
    }

    @Transactional
    public List<PostDto> findAllDescByTitle(String title) {
        return postRepository.findAllDescByTitleLike(title).stream().map(PostDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<PostDto> findAllDescByAuthor(String author) {
        return postRepository.findAllDescByAuthorNameLike(author).stream().map(PostDto::new).collect(Collectors.toList());
    }

    @Transactional
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new InvalidValueException("존재하지 않는 게시글입니다."));
    }

    @Transactional
    public PostDto modify(Long id, Member author, PostDto request) {
        request.validatePostCreateRequestDto();
        Post post = postRepository.findById(id).orElseThrow(() ->
                new InvalidValueException("This Post is not exist"));
        if (!author.getId().equals(post.getAuthor().getId()))
            throw new UnexpectedAccessException("It is not a author od this post");

        post.modify(request.getTitle(), request.getContent());
        return PostDto.builder().id(id).build();
    }

    @Transactional
    public PostDto remove(Long id, Member author) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new InvalidValueException("This Post is not exist"));
        if (!author.getId().equals(post.getAuthor().getId()))
            throw new UnexpectedAccessException("It is not a author od this post");

        postRepository.deleteById(id);
        return PostDto.builder().build();
    }

    @Transactional
    public boolean checkAuthor(Long id, Member author) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new InvalidValueException("This Post is not exist"));

        if (author.getId().equals(post.getAuthor().getId()))
            return true;
        else
            return false;
    }
}
