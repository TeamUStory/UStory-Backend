package com.elice.ustory.domain.user.constant;

public class EmailMessageConstants {
    public static final String NOT_FOUND_USER_EMAIL = "존재하지 않는 email입니다: %s";
    public static final String EMAIL_VERIFICATION_SENT = "가입된 이메일이라면 인증코드가 발송됩니다.";
    public static final String EMAIL_VERIFICATION_FAILED = "가입되지 않은 이메일이므로 메일이 발송되지 않았습니다. 보안을 위해, 사용자에게 해당 이메일의 가입 여부를 반환하지 않습니다.";
    public static final String EMAIL_CODE_NOT_MATCH = "인증 코드 요청이 주어진 이메일이지만, 인증 코드가 일치하지 않습니다.";
    public static final String EMAIL_CODE_VALID = "이메일과 인증 코드가 일치하여, 유효한 인증 코드로 검증되었습니다.";
    public static final String EMAIL_CODE_NONE = "인증 코드 요청이 오지 않은 이메일입니다.";
    public static final String EMAIL_CODE_NONE_DETAIL = "인증 코드 요청이 오지 않은 이메일입니다. 보안을 위해, 사용자에게 해당 이메일의 가입 여부를 반환하지 않습니다.";
    public static final String EMAIL_IN_USE = "사용중인_이메일";
    public static final String EMAIL_SOFT_DELETED = "탈퇴된_이메일";
    public static final String SUCCESS = "SUCCESS";
}
