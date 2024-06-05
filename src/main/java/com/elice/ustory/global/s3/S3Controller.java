package com.elice.ustory.global.s3;

import com.elice.ustory.global.s3.dto.presignedUrlResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "S3 Storage API")
@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/pre-signed-url/image/{fileName}")
    public ResponseEntity<presignedUrlResponse> getPresignedUrl(@PathVariable(name = "fileName")
                                                                @Schema(description = "확장자명을 포함해주세요. <br>jpg, jpeg, png, gif을 지원합니다.") String fileName) {
        presignedUrlResponse presignedUrlResponse = new presignedUrlResponse(s3Service.getPresignedUrl("images", fileName));
        return ResponseEntity.ok().body(presignedUrlResponse);
    }

}
