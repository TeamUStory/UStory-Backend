package com.elice.ustory.domain.notice.repository;

import com.elice.ustory.domain.notice.entity.MessageType;
import com.elice.ustory.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findBySenderIdAndReceiverIdAndMessageType(Long senderId, Long receiverId, MessageType messageType);
    List<Notice> findByReceiverId(Long receiverId);
}
