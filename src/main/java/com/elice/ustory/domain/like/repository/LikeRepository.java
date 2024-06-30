package com.elice.ustory.domain.like.repository;

import com.elice.ustory.domain.like.entity.Like;
import com.elice.ustory.domain.like.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId>, LikeQueryDslRepository {

    /** userId와 paperId가 일치하는 Like 가져오기 */
    Optional<Like> findByUserIdAndPaperId(Long userId, Long paperId);
}
