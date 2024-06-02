package com.elice.ustory.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Paper Id와 이미지 순서를 통해서 Image를 불러오는 쿼리
     *
     * @param paperId   페이퍼 Id
     * @param sequence  이미지의 순서
     * @return
     */
    @Query("SELECT i FROM Image i WHERE i.paper.id = :paperId AND i.sequence = :sequence")
    Optional<Image> findByPaperIdAndSequence(@Param("paperId") Long paperId, @Param("sequence") int sequence);

    /**
     * Paper Id에 해당하는 Image들을 정렬하여 불러오는 쿼리
     *
     * @param paperId   페이퍼 Id
     * @return
     */
    List<Image> findByPaperIdOrderBySequenceAsc(Long paperId);
}
