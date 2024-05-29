package com.elice.ustory.domain.user.repository;

import com.elice.ustory.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByNickname(String nickname);

    List<Users> findByNicknameContaining(String nickname);

}
