package com.elice.ustory.global.util;

//import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.domain.notice.dto.PaperNoticeRequest;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;

public class CommonUtils {
    // 알림 메시지 상수
    public static final String FRIEND_REQUEST_MESSAGE = "친구 요청이 있습니다.";
    public static final String COMMENT_REQUEST_MESSAGE = "당신의 코멘트가 필요해요!";
    public static final String FRIEND_ACCEPT_MESSAGE = "%s님이 친구를 수락하였습니다.";
    public static final String PAPER_OPEN_MESSAGE = "페이퍼 오픈!";

    /**
     * 메시지 생성 메서드
     *
//     * @param noticeDTO 알림 DTO
     * @return 생성된 메시지
     */

    public static String generateMessage(NoticeRequest noticeRequest) {
        int messageType = noticeRequest.getMessageType();

        return switch (messageType) {
            case 1 -> FRIEND_REQUEST_MESSAGE;
            case 2 -> COMMENT_REQUEST_MESSAGE;
            case 4 -> PAPER_OPEN_MESSAGE;
            default -> throw new UnauthorizedException("잘못된 메세지 타입입니다.", ErrorCode.VALIDATION_EXCEPTION);
        };
    }
    public static String generateMessage(NoticeRequest noticeRequest, String nickname) {
                return generateFriendAcceptMessage(noticeRequest, nickname);
    }

    /**
     * 친구 수락 알림 메시지 생성 메서드
     *
//     * @param friendNoticeDTO 친구 수락 알림 DTO
     * @return 생성된 친구 수락 메시지
     */
    public static String generateFriendAcceptMessage(NoticeRequest noticeRequest, String nickname) {
        return String.format(FRIEND_ACCEPT_MESSAGE, nickname);
    }

    /**
     * 알림 DTO에서 senderId를 추출합니다.
     *
     * @param noticeDTO 알림 DTO
     * @return senderId
     */
//    public static Long extractSenderId(NoticeDTO noticeDTO) {
//        if (noticeDTO instanceof FriendNoticeDTO) {
//            return ((FriendNoticeDTO) noticeDTO).getSenderId();
//        } else if (noticeDTO instanceof PaperNoticeRequest){
//            return ((PaperNoticeRequest) noticeDTO).getPaper().getId();
//        }
//        return null;
//    }


    /**
     * 알림 DTO에서 Paper 객체를 추출합니다.
     *
     * @param noticeDTO 알림 DTO
     * @param
     * @return Paper 객체
     */
//    public static Paper extractPaper(NoticeDTO noticeDTO) {
//        if (noticeDTO instanceof PaperNoticeRequest) {
//            return ((PaperNoticeRequest) noticeDTO).getPaper();
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
//    public static Notice createNotice(NoticeDTO noticeDTO, String message, Long senderId) {
//        return Notice.builder()
//                .receiverId(noticeDTO.getReceiverId())
//                .senderId(senderId)
//                .message(message)
//                .messageType(noticeDTO.getMessageType())
//                .build();
//    }


    /**
     * 친구 요청 ID 생성
     *
     * @param senderId   친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @return FriendId 객체
     */
    public static FriendId createFriendId(Long senderId, Long receiverId) {
        return new FriendId(senderId, receiverId);
    }




    /**
     * FriendNoticeDTO 생성 메서드
     *
     * @param receiverId 수신자의 ID
     * @param senderId 발신자의 ID
     * @param messageType 메시지 타입
     * @param nickname 닉네임
     * @param invitedAt 초대 시간
     * @param acceptedAt 수락 시간
     * @return FriendNoticeDTO 객체
     */
//    public static FriendNoticeDTO createFriendNoticeDTO(Long receiverId, Long senderId, int messageType, String nickname, LocalDateTime invitedAt, LocalDateTime acceptedAt) {
//        return FriendNoticeDTO.builder()
//                .receiverId(receiverId)
//                .senderId(senderId)
//                .messageType(messageType)
//                .invitedAt(invitedAt)
//                .acceptedAt(acceptedAt)
//                .nickname(nickname)
//                .build();
//    }



}
