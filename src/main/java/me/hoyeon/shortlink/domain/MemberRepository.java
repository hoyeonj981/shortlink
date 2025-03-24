package me.hoyeon.shortlink.domain;

import java.util.Optional;

public interface MemberRepository {

  Optional<UnverifiedMember> findUnverifiedById(Long memberId);

  void save(VerifiedMember verifiedMember);

  Optional<Member> findByEmail(Email email);
}
