package com.ogu.ogoods.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class MemberFormDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    // NotBlank : Null값 비허용 ("", " " 모두 비허용)
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 4자 이상, 12자 이하의 영문 및 숫자로 이루어져야 합니다.")
    private String mid;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영어와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    // @Length(min=8, max=20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요")
    private String password;

    @NotBlank(message = "비밀번호를 다시 한 번 입력해주세요.")
    // @Length(min=8, max=20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요")
    private String confirmPassword;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    private String phone;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
}
