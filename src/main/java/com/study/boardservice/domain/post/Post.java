package com.study.boardservice.domain.post;

import com.study.boardservice.domain.comment.Comment;
import com.study.boardservice.domain.like.Recommend;
import com.study.boardservice.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Recommend> recommends = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Builder
    public Post(String title, String content, Member author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addRecommend(Recommend recommend){
        recommends.add(recommend);
    }

    public void removeRecommend(Recommend recommend){
        recommends.remove(recommend);
    }

    public void addComment(Comment comment){
        comments.add(comment);
    }
}
