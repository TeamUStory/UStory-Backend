package com.elice.ustory.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
    RED("#FDD0B1")
    ,ORANGE("#F9EFC7")
    ,YELLOW("#C3EDBF")
    ,GREEN("#B8DFE6")
    ,BLUE("#B8DFE6")
    ,INDIGO("#A3C4F3")
    ,PURPLE("#C5BBDE")
    ,BLACK("#656565")
    ,GRAY("#DADADA")
    ,WHITE("#FFFFFF")
    ,;

    private final String hexCode;
}
