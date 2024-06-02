package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;

public interface PaperQueryDslRepository {

    /** 다이어리에 포함되는 Paper를 불러온다. <br>
     *  지정된 범위가 존재한다면 범위에 맞춰서 최신순으로 불러온다. */
    Slice<Paper> findAllByDiaryIdAndDateRange(Long diaryId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
