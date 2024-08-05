package com.elice.ustory.domain.great.repository;

import com.elice.ustory.domain.great.entity.Great;
import com.elice.ustory.domain.great.entity.GreatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GreatRepository extends JpaRepository<Great, GreatId>, GreatQueryDslRepository {

    /** userId와 paperId가 일치하는 Great 가져오기 */
    Optional<Great> findByUserIdAndPaperId(Long userId, Long paperId);
}
