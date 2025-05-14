package me.hoyeon.shortlink.domain;

import java.util.Optional;

public interface MemberRepository {

  Optional<Member> findById(Long memberId);

  Optional<UnverifiedMember> findUnverifiedById(Long memberId);

  void save(VerifiedMember verifiedMember);

  void save(UnverifiedMember unverifiedMember);

  Optional<Member> findByEmail(Email email);

  boolean existsByEmail(Email email);
}
