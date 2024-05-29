package com.elice.ustory.domain.notice.service;

import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.elice.ustory.global.util.NotificationUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private FriendService friendService;



    @Autowired
    public NoticeService(NoticeRepository noticeRepository, UserRepository userRepository, PaperRepository paperRepository, @Lazy FriendService friendService) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
        this.friendService = friendService;
    }

    /**
     * 특정 사용자의 모든 알림을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 알림 목록
     */
    public List<Notice> getAllNoticesByUserId(Long userId) {
        return noticeRepository.findByReceiverId(userId);
    }



    /**
     * 공통 알림 전송 메서드
     *
     * @param noticeDTO 알림 DTO
     */
    public void sendNotice(NoticeDTO noticeDTO) {
        String message = NotificationUtils.generateMessage(noticeDTO);
        Long senderId = NotificationUtils.extractSenderId(noticeDTO);
//        Paper paper = NotificationUtils.extractPaper(noticeDTO, paperRepository);

        Notice notice = NotificationUtils.createNotice(noticeDTO, message, senderId);
//        notice.setPaper(paper);

        // populateNotice 호출하여 필요한 값 설정
        noticeDTO.populateNotice(notice);

        // 로그 추가
        System.out.println("Notice before save: " + notice);

        // 알림 저장
        noticeRepository.save(notice);
    }



    /**
     * 알림을 ID로 삭제합니다.
     *
     * @param id 삭제할 알림의 ID
     */
    public void deleteNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));
        noticeRepository.delete(notice);
    }

    /**
     * 특정 조건으로 알림을 삭제합니다 (senderId 기준).
     *
     * @param senderId 알림을 보낸 사용자의 ID
     * @param receiverId 알림을 받은 사용자의 ID
     * @param messageType 알림의 유형
     */
    public void deleteNoticeBySender(Long senderId, Long receiverId, int messageType) {
        Optional<Notice> noticeOptional = noticeRepository.findBySenderIdAndReceiverIdAndMessageType(senderId, receiverId, messageType);
        noticeOptional.ifPresent(noticeRepository::delete);
    }

    /**
     * 특정 조건으로 알림을 삭제합니다 (paperId 기준).
     *
     * @param paperId 알림과 관련된 페이퍼 ID
     * @param receiverId 알림을 받은 사용자의 ID
     * @param messageType 알림의 유형
     */
    public void deleteNoticeByPaper(Long paperId, Long receiverId, int messageType) {
        Optional<Notice> noticeOptional = noticeRepository.findByPaperIdAndReceiverIdAndMessageType(paperId, receiverId, messageType);
        noticeOptional.ifPresent(noticeRepository::delete);
    }





}
