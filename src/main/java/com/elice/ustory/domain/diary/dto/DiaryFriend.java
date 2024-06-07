package com.elice.ustory.domain.diary.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryFriend {
    private String nickname;
    private String profileImgUrl;

    public DiaryFriend(String nickname,String profileImgUrl){
        this.nickname=nickname;
        this.profileImgUrl=profileImgUrl;
    }
}
