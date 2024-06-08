package com.elice.ustory.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // Validation Exception: 400
    VALIDATION_EXCEPTION("V001", "잘못된 요청입니다."),
    MISSING_REQUIRED_PARAMETER("V002", "필수 파라미터가 누락되었습니다."),
    VALIDATION_PARAMETER_EXCEPTION("V003", "파라미터 값이 잘못되었습니다."),
    PARAMETER_INCORRECT_FORMAT("V004", "파라미터가 잘못된 형식입니다."),

    // Unauthorized Exception: 401,
    UNAUTHORIZED_EXCEPTION("U001", "인증되지 않은 접근입니다."),
    EXPIRED_TOKEN_EXCEPTION("U002", "토큰 값이 만료되었습니다, 토큰을 갱신해주세요."),
    EXPIRED_REFRESH_TOKEN_EXCEPTION("U003", "Refresh 토큰이 만료되었습니다, 재로그인해주세요."),

    // Forbidden Exception: 403
    FORBIDDEN_EXCEPTION("F001", "허용하지 않는 접근입니다."),

    // NotFound Exception: 404
    NOT_FOUND_EXCEPTION("N001", "요청한 리소스를 찾을 수 없습니다."),

    // Method Not Allowed Exception: 405
    METHOD_NOT_ALLOWED_EXCEPTION("M001", "허용되지 않은 메서드입니다."),

    // Conflict Exception: 409
    CONFLICT_EXCEPTION("C001", "중복된 정보입니다."),

    // Unsupported Media Type Exception: 415
    UNSUPPORTED_MEDIA_TYPE_EXCEPTION("MT01", "지원되지 않는 미디어 타입입니다."),

    // Internal Server Exception: 500
    INTERNAL_SERVER_EXCEPTION("I001", "서버 내부에서 에러가 발생하였습니다."),
    ;

    private final String code;
    private final String message;
}
