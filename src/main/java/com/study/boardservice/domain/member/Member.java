package com.study.boardservice.domain.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;

    @Builder
    public Member(String email, String password, String name, String phone){
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public void modifyProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void modifyPassword(String password) {
        this.password = password;
    }

    public void encodePassword() {
        password = new BCryptPasswordEncoder().encode(password);
    }

    public boolean matchPassword(String password) {
        return new BCryptPasswordEncoder().matches(password, this.password);
    }
}
