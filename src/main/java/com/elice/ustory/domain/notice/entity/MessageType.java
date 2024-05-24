package com.elice.ustory.domain.notice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    Friend("님이 친구 신청을 하였습니다.")
    , Paper("한줄평 남겨주세요!");

    private final String messageTemplate;

    public String createMessage(String senderNickname) {
        return senderNickname + this.messageTemplate;
    }
}
