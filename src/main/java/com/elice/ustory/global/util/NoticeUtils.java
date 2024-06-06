package com.elice.ustory.global.util;


import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.global.exception.model.ValidationException;


public class NoticeUtils {
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
        return switch (noticeRequest.getMessageType()) {
            case 1 -> FRIEND_REQUEST_MESSAGE;
            case 2 -> COMMENT_REQUEST_MESSAGE;
            case 4 -> PAPER_OPEN_MESSAGE;
            default -> throw new ValidationException("잘못된 메시지 타입입니다.");
        };
    }

    public static String generateMessage(NoticeRequest noticeRequest, String nickname) {
        return String.format(FRIEND_ACCEPT_MESSAGE, nickname);
    }


}
