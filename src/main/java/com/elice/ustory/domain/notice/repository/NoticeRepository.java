package com.elice.ustory.domain.notice.repository;

import com.elice.ustory.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeQueryDslRepository {
    Optional<Notice> findByRequestIdAndResponseIdAndMessageType(Long requestId, Long responseId, int messageType);
    List<Notice> findByResponseId(Long responseId);
}
