package com.elice.ustory.domain.diaryUser.repository;

import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryUserRepository extends JpaRepository<DiaryUser, DiaryUserId>, DiaryUserQueryDslRepository {
}
