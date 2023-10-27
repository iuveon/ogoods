package com.ogu.ogoods.entity;

import com.ogu.ogoods.constant.Role;
import com.ogu.ogoods.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseTimeEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 번호

    @Column(unique = true)
    private String mid; // 사용자 아이디

    private String password; // 사용자 비밀번호

    private String name; // 사용자 이름

    @Column(unique = true)
    private String email; // 사용자 메일

    @Column(unique = true)
    private String phone; // 사용자 연락처

    private String address; // 사용자 주소

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, USER

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setMid(memberFormDto.getMid());
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setPhone(memberFormDto.getPhone());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        // member.setRole(Role.USER); // USER Role 부여
        member.setRole(Role.ADMIN);
        return member;
    }
}