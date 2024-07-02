package com.elice.ustory.domain.user.repository;

import com.elice.ustory.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByNickname(String nickname);
    Optional<Users> findByEmail(String loginEmail);
    boolean existsByEmail(String loginEmail);

    @Query(value = "SELECT COUNT(*) FROM users u WHERE u.email = :email", nativeQuery = true)
    int countByEmailWithSoftDeleted(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) FROM users u WHERE u.nickname = :nickname", nativeQuery = true)
    int countByNicknameWithSoftDeleted(@Param("nickname") String nickname);
}
