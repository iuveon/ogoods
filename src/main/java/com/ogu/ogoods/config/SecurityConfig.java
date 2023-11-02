package com.ogu.ogoods.config;

import com.ogu.ogoods.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // @Bean을 이용하여 수동으로 빈 등록 가능
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login") // 로그인 페이지 URL 설정
                .defaultSuccessUrl("/") // 로그인 성공 시 이동할 URL -> main 페이지
                .usernameParameter("mid") // 로그인 시 사용할 파라미터 이름 mid로 설정
                .failureUrl("/members/login/error") // 로그인 실패 시 이동할 URL
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃 URL
                .logoutSuccessUrl("/"); // 로그아웃 성공 시 이동할 URL

        http.authorizeRequests() // 요청에 대한 인증 및 권한 설정
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() // permitAll() : 모든 사용자 접근 허용
                .mvcMatchers("/admin/**").hasRole("ADMIN") // ADMIN Role인 사용자만 접근 허용
                .anyRequest().authenticated(); // 그 외 요청은 인증된 사용자만 가능

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        // 인증되지 않은 사용자의 접근 시 수행되는 핸들러 -> CustomAuthenticationEntryPoint로 보냄
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 해시함수를 이용하여 비밀번호 암호화
    }

    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }
     */

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/images/**");
        // 보안 필터링을 적용하지 않을 리소스의 경로 패턴을 지정
    }

}
