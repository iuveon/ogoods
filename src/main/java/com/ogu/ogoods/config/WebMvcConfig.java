package com.ogu.ogoods.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}") // application.properties에 설정한 프로퍼티 값 읽어옴
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // 웹 브라우저 입력 URL이 /images로 시작하는 경우
                .addResourceLocations(uploadPath); // 로컬 컴퓨터에 저장된 파일 읽어올 root 경로 설정
    }
}
