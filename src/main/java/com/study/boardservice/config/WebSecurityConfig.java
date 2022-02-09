package com.study.boardservice.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/v1/member/signup", "/api/v1/member/login", "/api/v1/member/id"
                        ,"/api/v1/member/email", "/api/v1/member/password", "/api/v1/member/logout").permitAll()
                .antMatchers("/api/v1/posts", "/api/v1/posts/*", "/api/v1/posts/*/recommend", "/api/v1/posts/*/comments").permitAll()
                .antMatchers("/css/**", "/js/**", "/", "/signup", "/login", "/find-email", "/get-email"
                        , "/find-password", "/change-password", "/create", "/board", "/posts/*", "/posts/*/update").permitAll()
                .anyRequest().authenticated();
    }
}
