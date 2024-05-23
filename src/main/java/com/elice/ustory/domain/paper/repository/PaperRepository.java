package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
}
