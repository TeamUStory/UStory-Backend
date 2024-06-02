package com.elice.ustory.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Paper Id에 해당하는 Image들을 정렬하여 불러오는 쿼리
     *
     * @param paperId   페이퍼 Id
     * @return
     */
    List<Image> findByPaperIdOrderBySequenceAsc(Long paperId);
}
