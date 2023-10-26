package com.ogu.ogoods.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 설정 클래스임을 명시, @Bean을 사용하여 Bean 객체 생성하게 함
@EnableJpaAuditing // JPA의 Auditing 활성화
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
