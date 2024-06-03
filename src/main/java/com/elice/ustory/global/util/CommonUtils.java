package com.elice.ustory.global.util;


import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.UnauthorizedException;


public class CommonUtils {
    // 알림 메시지 상수
    public static final String FRIEND_REQUEST_MESSAGE = "친구 요청이 있습니다.";
    public static final String COMMENT_REQUEST_MESSAGE = "당신의 코멘트가 필요해요!";
    public static final String FRIEND_ACCEPT_MESSAGE = "%s님이 친구를 수락하였습니다.";
    public static final String PAPER_OPEN_MESSAGE = "페이퍼 오픈!";

    /**
     * 메시지 생성 메서드
     *
     * @param noticeRequest 알림 DTO
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
     * @param noticeRequest 친구 수락 알림 DTO
     * @return 생성된 친구 수락 메시지
     */
    public static String generateFriendAcceptMessage(NoticeRequest noticeRequest, String nickname) {
        return String.format(FRIEND_ACCEPT_MESSAGE, nickname);
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

}
