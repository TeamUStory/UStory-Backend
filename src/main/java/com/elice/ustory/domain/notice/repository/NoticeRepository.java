package com.elice.ustory.domain.notice.repository;

import com.elice.ustory.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findBySenderIdAndReceiverIdAndMessageType(Long senderId, Long receiverId, int messageType);
    Optional<Notice> findByPaperIdAndReceiverIdAndMessageType(Long paperId, Long receiverId, int messageType);
    List<Notice> findByReceiverId(Long receiverId);
}
