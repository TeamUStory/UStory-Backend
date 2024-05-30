package com.elice.ustory.domain.notice.service;

import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.paper.dto.PaperNoticeRequest;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private FriendService friendService;



    // 알림 메시지 상수
    private static final String FRIEND_REQUEST_MESSAGE = "친구 요청이 있습니다.";
    private static final String COMMENT_REQUEST_MESSAGE = "당신의 코멘트가 필요해요!";
    private static final String FRIEND_ACCEPT_MESSAGE = "%s님이 친구를 수락하였습니다.";
    private static final String PAPER_OPEN_MESSAGE = "페이퍼 오픈!";


    @Autowired
    public NoticeService(NoticeRepository noticeRepository, UserRepository userRepository, @Lazy FriendService friendService) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
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
        String message = generateMessage(noticeDTO);
        Long senderId = extractSenderId(noticeDTO);
        Paper paper = extractPaper(noticeDTO);

        Notice notice = Notice.builder()
                .receiverId(noticeDTO.getReceiverId())
                .senderId(senderId)
                .paper(paper)
                .message(message)
                .messageType(noticeDTO.getMessageType())
                .build();

        // populateNotice 호출하여 필요한 값 설정
        noticeDTO.populateNotice(notice);

        // 로그 추가
        System.out.println("Notice before save: " + notice);

        noticeRepository.save(notice);
    }

    /**
     * 알림 DTO에서 senderId를 추출합니다.
     *
     * @param noticeDTO 알림 DTO
     * @return senderId
     */
    private Long extractSenderId(NoticeDTO noticeDTO) {
        if (noticeDTO instanceof FriendNoticeDTO) {
            return ((FriendNoticeDTO) noticeDTO).getSenderId();
        }
        return null;
    }

    /**
     * 알림 DTO에서 Paper 객체를 추출합니다.
     *
     * @param noticeDTO 알림 DTO
     * @return Paper 객체
     */
    private Paper extractPaper(NoticeDTO noticeDTO) {
        if (noticeDTO instanceof PaperNoticeRequest) {
            return ((PaperNoticeRequest) noticeDTO).getPaper();
        }
        return null;
    }






    /**
     * 메시지 생성 메서드
     *
     * @param noticeDTO 알림 DTO
     * @return 생성된 메시지
     */
    private String generateMessage(NoticeDTO noticeDTO) {
        int messageType = noticeDTO.getMessageType();

        switch (messageType) {
            case 1:
                return FRIEND_REQUEST_MESSAGE;
            case 2:
                return COMMENT_REQUEST_MESSAGE;
            case 3:
                return generateFriendAcceptMessage((FriendNoticeDTO) noticeDTO);
            case 4:
                return PAPER_OPEN_MESSAGE;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown message type");
        }
    }

    private String generateFriendAcceptMessage(FriendNoticeDTO friendNoticeDTO) {
        String receiverNickname = friendNoticeDTO.getNickname();
        return String.format(FRIEND_ACCEPT_MESSAGE, receiverNickname);
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
