package com.elice.ustory.global.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final String MISSING_IMAGE_EXTENSION_EXCEPTION_MESSAGE = "확장자가 누락되었습니다.";
    private static final String INVALID_IMAGE_EXTENSION_EXCEPTION_MESSAGE = "jpg, jpeg, png, gif 확장자만을 지원합니다.";
    private static final String IMAGE_URL_PATH_FORMAT = "%s/%s";

    @Value("${cloud.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public String getPresignedUrl(String prefix, String fileName) {

        validateFileName(fileName);

        if (!prefix.isEmpty()) {
            fileName = createPath(prefix, fileName);
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    private void validateFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (extension.equals(fileName)) {
            throw new ValidationException(MISSING_IMAGE_EXTENSION_EXCEPTION_MESSAGE, ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ValidationException(INVALID_IMAGE_EXTENSION_EXCEPTION_MESSAGE, ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    /** PreSigned URL 만료시간 설정, 2분으로 지정되어 있음*/
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    /** URL 형식 생성, {폴더명}/{RandomId값}-{파일명} */
    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format(IMAGE_URL_PATH_FORMAT, prefix, fileId + "-" + fileName);
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }
}
