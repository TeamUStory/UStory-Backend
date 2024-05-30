package com.elice.ustory.global.util;

import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.user.entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
     * @param
     * @return Paper 객체
     */
//    public static Paper extractPaper(NoticeDTO noticeDTO) {
//
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
     * 친구 엔티티 생성
     *
     * @param sender    친구 요청을 보낸 사용자
     * @param receiver  친구 요청을 받은 사용자
     * @param friendId  친구 요청 ID
     * @param status    친구 요청 상태
     * @param invitedAt 친구 요청 시간
     * @param acceptedAt 친구 수락 시간
     * @return Friend 객체
     */
    public static Friend createFriendEntity(Users sender, Users receiver, FriendId friendId, FriendStatus status, LocalDateTime invitedAt, LocalDateTime acceptedAt) {
        return Friend.builder()
                .id(friendId)
                .invitedAt(invitedAt)
                .acceptedAt(acceptedAt)
                .user(sender)
                .friendUser(receiver)
                .status(status)
                .build();
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
    public static FriendNoticeDTO createFriendNoticeDTO(Long receiverId, Long senderId, int messageType, String nickname, LocalDateTime invitedAt, LocalDateTime acceptedAt) {
        return FriendNoticeDTO.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .messageType(messageType)
                .invitedAt(invitedAt)
                .acceptedAt(acceptedAt)
                .nickname(nickname)
                .build();
    }



}
