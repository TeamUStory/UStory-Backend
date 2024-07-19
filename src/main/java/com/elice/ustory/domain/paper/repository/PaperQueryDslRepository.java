package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.paper.entity.Paper;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaperQueryDslRepository {

    /** 다이어리에 포함되는 Paper를 불러온다. <br>
     *  지정된 범위가 존재한다면 범위에 맞춰서 불러온다. <br>
     *  정렬 기준은 생성날짜이다.*/
    List<Paper> findAllByDiaryIdAndDateRange(Long diaryId, LocalDateTime requestTime, Pageable pageable, LocalDate startDate, LocalDate endDate);

    /** 사용자가 포함되어 있는 다이어리의 모든 Paper 리스트 불러오기 */
    List<Paper> findAllPapersByUserId(Long userId);

    /** 본인이 작성한 Paper를 불러온다. <br>
     *  정렬 기준은 생성날짜이다.
     */
    List<Paper> findByWriterId(Long writerId, LocalDateTime requestTime, Pageable pageable);

    List<Paper> joinPaperByAddress(Address address);
}
