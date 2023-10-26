package com.ogu.ogoods.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    // AuditorAware : 엔티티의 생성자와 수정자 자동 관리 interface
    @Override
    public Optional<String> getCurrentAuditor() {
        // Optional : NPE(NullPointerException) 방지 wrapper 클래스
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        /* SecurityContextHolder : 스프링 시큐리티의 SecurityContext 관리
        SecurityContextHolder.getContext().getAuthentication() -> 사용자 인증정보 가져오기 */
        String userId = "";
        if(authentication != null) { // 사용자 인증정보가 null이 아니라면
            userId = authentication.getName(); // 사용자의 ID 가져와서 userId에 저장
        }
        return Optional.of(userId); // userId를 Optional로 감싸서 리턴
    }
}
