package com.elice.ustory.domain.grate.repository;

import com.elice.ustory.domain.grate.entity.Grate;
import com.elice.ustory.domain.grate.entity.GrateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrateRepository extends JpaRepository<Grate, GrateId>, GrateQueryDslRepository {

    /** userId와 paperId가 일치하는 Grate 가져오기 */
    Optional<Grate> findByUserIdAndPaperId(Long userId, Long paperId);
}
