package com.ogu.ogoods.service;

import com.ogu.ogoods.entity.Member;
import com.ogu.ogoods.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByMid(member.getMid());
        if(findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {
        Member member = memberRepository.findByMid(mid);

        if(member == null) {
            throw new UsernameNotFoundException(mid);
        }
        return User.builder()
                .username(member.getMid())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
