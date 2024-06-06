package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long>, PaperQueryDslRepository {

    List<Paper> findByWriterId(Long writerId);
    List<Paper> findByWriterId(Long writerId, Pageable pageable);

}

