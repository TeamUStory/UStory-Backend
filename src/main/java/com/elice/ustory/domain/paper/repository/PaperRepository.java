package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {

    List<Paper> findByDiaryId(Long diaryId, Pageable pageable);

    List<Paper> findByWriterId(Long writerId, Pageable pageable);
}
