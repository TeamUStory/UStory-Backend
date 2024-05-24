package com.elice.ustory.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaryCategory {

    INDIVIDUAL("개인")
    ,COUPLE("연인")
    ,FAMILY("가족")
    ,FRIEND("친구")
    ,US("어스")
    ,;

    private final String name;
}