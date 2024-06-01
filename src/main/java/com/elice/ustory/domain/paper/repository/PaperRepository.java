package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {

    List<Paper> findByDiaryId(Long diaryId, Pageable pageable);

    List<Paper> findByWriterId(Long writerId);
    List<Paper> findByWriterId(Long writerId, Pageable pageable);

    /** 사용자가 포함되어 있는 다이어리의 모든 Paper 리스트 불러오기 */
    @Query("SELECT p FROM Paper p WHERE p.diary.id IN (SELECT du.id.diary.id FROM DiaryUser du WHERE du.id.users.id = :userId)")
    List<Paper> findAllPapersByUserId(@Param("userId") Long userId);
}

