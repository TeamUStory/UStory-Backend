package com.elice.ustory.global.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class presignedUrlResponse {
    @Schema(description = "PreSignedUrl <br> PUT 메서드로 해당 URL에 이미지를 첨부하여 요청해야한다. <br> 200상태값을 반환하면 쿼리 파라미터 부분을 제외한 부분이 해당 이미지의 URL이 된다.",
            example = "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/images/276ae3f4-a874-4cd8-a21d-ef665017686d-userProfile.png?x-amz-acl=public-read&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240605T195107Z&X-Amz-SignedHeaders=host&X-Amz-Expires=119&X-Amz-Credential=AKIAYS2NTODI5M4KUV7I%2F20240605%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=5c96574cb47d2c42586204245e26d8d71813440bd75bd1b802d1745a60f71136")
    private String presignedUrl;
}
