package com.ogu.ogoods.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Spring Boot 테스트 프레임워크와 함께 MockMvc를 자동으로 구성하는 데 사용
@TestPropertySource(locations="classpath:application-test.properties") // 테스트 환경에서 사용할 property 소스 지정
public class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("상품 등록 페이지 권한 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    // 현재 로그인한 사용자를 임의로 생성 -> "admin"이라는 사용자명과 "ADMIN"이라는 역할(권한) 부여
    public void itemFormTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new")) // URL로 GET요청 보냄
                    .andDo(print()) // 수행한 요청과 응답을 콘솔로 출력
                    .andExpect(status().isOk()); // 페이지에 정상적으로 접근 가능한지 확인
    }

    @Test
    @DisplayName("상품 등록 페이지 일반 회원 접근 테스트")
    @WithMockUser(username = "user", roles = "USER")
    // 현재 로그인한 사용자를 임의로 생성 -> "user"이라는 사용자명과 "USER"이라는 역할(권한) 부여
    public void itemFormNotAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new")) // URL로 GET요청 보냄
                    .andDo(print()) // 수행한 요청과 응답을 콘솔로 출력
                    .andExpect(status().isForbidden()); // 페이지 진입 요청 시 Forbidden 예외 발생하는지 확인
    }

}
