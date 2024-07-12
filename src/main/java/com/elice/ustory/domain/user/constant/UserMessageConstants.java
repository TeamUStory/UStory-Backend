package com.elice.ustory.domain.user.constant;

public class UserMessageConstants {
    public static final String NOT_FOUND_USER_ID_MESSAGE = "존재하지 않는 userId입니다: %d";
    public static final String NOT_FOUND_USER_EMAIL_MESSAGE = "존재하지 않는 email입니다: %s";
    public static final String NOT_VALID_NICKNAME_MESSAGE = "사용할 수 없는 nickname입니다: %s";
    public static final String NOT_APPROPIRATE_NICKNAME_MSSAGE = "닉네임 형식이 올바르지 않습니다.";
    public static final String DUPLICATE_EMAIL_MESSAGE = "이미 가입된 email입니다: %s";
    public static final String NOT_CREATED_DIARY_MESSAGE = "다음의 email로 가입 중인 유저의, 개인 다이어리를 생성하는 과정에서 문제가 발생하였습니다. 가입 정보는 저장되지 않습니다: %s";
    public static final String UNAUTHORIZED_MESSAGE = "헤더에 토큰이 입력되지 않았습니다.";
    public static final String NOT_VALID_PASSWORD_MESSAGE = "비밀번호 확인이 일치하지 않습니다.";
}
