package com.ogu.ogoods.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MemberFormDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    // NotBlank : Null값 비허용 ("", " " 모두 비허용)
    @Length(min=4, max=12, message = "아이디는 4자 이상, 12자 이하로 입력해주세요")
    // @Length : 필드 크기 검증
    private String mid;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=8, max=20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    private String phone;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
}
