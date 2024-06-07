package com.elice.ustory.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
    RED("#FBB9C5")
    ,ORANGE("#FDD0B1")
    ,YELLOW("#F9EFC7")
    ,GREEN("#C3EDBF")
    ,BLUE("#B8DFE6")
    ,INDIGO("#A3C4F3")
    ,PURPLE("#C5BBDE")
    ,BLACK("#656565")
    ,GRAY("#DADADA")
    ,WHITE("#FFFFFF")
    ,;

    private final String hexCode;
}
