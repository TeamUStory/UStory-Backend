package com.elice.ustory.global.util;


import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 알림 메시지 생성
 * 유효성 검사
 * 공통 알림 로직
 */
public class NotificationUtils {

    // 알림 메시지 상수
    public static final String FRIEND_REQUEST_MESSAGE = "친구 요청이 있습니다.";
    public static final String COMMENT_REQUEST_MESSAGE = "당신의 코멘트가 필요해요!";
    public static final String FRIEND_ACCEPT_MESSAGE = "%s님이 친구를 수락하였습니다.";
    public static final String PAPER_OPEN_MESSAGE = "페이퍼 오픈!";

    /**
     * 메시지 생성 메서드
     *
     * @param noticeDTO 알림 DTO
     * @return 생성된 메시지
     */
    public static String generateMessage(NoticeDTO noticeDTO) {
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

    /**
     * 친구 수락 알림 메시지 생성 메서드
     *
     * @param friendNoticeDTO 친구 수락 알림 DTO
     * @return 생성된 친구 수락 메시지
     */
    public static String generateFriendAcceptMessage(FriendNoticeDTO friendNoticeDTO) {
        String receiverNickname = friendNoticeDTO.getNickname();
        return String.format(FRIEND_ACCEPT_MESSAGE, receiverNickname);
    }


    /**
     * 알림 DTO에서 senderId를 추출합니다.
     *
     * @param noticeDTO 알림 DTO
     * @return senderId
     */
    public static Long extractSenderId(NoticeDTO noticeDTO) {
        if (noticeDTO instanceof FriendNoticeDTO) {
            return ((FriendNoticeDTO) noticeDTO).getSenderId();
        }
        return null;
    }

    /**
     * 알림 DTO에서 Paper 객체를 추출합니다.
     *
     * @param noticeDTO 알림 DTO
     * @param paperRepository Paper 리포지토리
     * @return Paper 객체
     */
//    public static Paper extractPaper(NoticeDTO noticeDTO, PaperRepository paperRepository) {
//        if (noticeDTO instanceof PaperNoticeDTO) {
//            Long paperId = ((PaperNoticeDTO) noticeDTO).getPaperId();
//            return paperRepository.findById(paperId)
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));
//        }
//        return null;
//    }



    /**
     * Notice 객체 생성
     *
     * @param noticeDTO 알림 DTO
     * @param message 메시지 내용
     * @param senderId 보낸 사람 ID
     * @return 생성된 Notice 객체
     */
    public static Notice createNotice(NoticeDTO noticeDTO, String message, Long senderId) {
        return Notice.builder()
                .receiverId(noticeDTO.getReceiverId())
                .senderId(senderId)
                .message(message)
                .messageType(noticeDTO.getMessageType())
                .build();
    }


}
