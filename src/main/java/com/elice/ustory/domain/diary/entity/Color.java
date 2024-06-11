package com.elice.ustory.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
    RED("#FBB9C5", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/red-marker.png")
    ,ORANGE("#FDD0B1", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/orange-marker.png")
    ,YELLOW("#F9EFC7", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/yellow-marker.png")
    ,GREEN("#C3EDBF", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/green-marker.png")
    ,BLUE("#B8DFE6", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/blue-marker.png")
    ,INDIGO("#A3C4F3", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/indigo-marker.png")
    ,PURPLE("#C5BBDE", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/purple-marker.png")
    ,BLACK("#656565", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/black-marker.png")
    ,GRAY("#DADADA", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/gray-marker.png")
    ,WHITE("#FFFFFF", "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/white-marker.png")
    ,;

    private final String hexCode;
    private final String markerUrl;
}
