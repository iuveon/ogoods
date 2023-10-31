package com.ogu.ogoods.repository;

import com.ogu.ogoods.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMid(String mid);

    boolean existsByMid(String mid);
}
