package com.elice.ustory.domain.user.constant;

public class RegexPatterns {
    public static final String EMAIL_REG = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";


    // 유저 비밀번호 규칙: 숫자, 영문, 특수문자 각 1개를 포함한 8~16자. 보안상 SQL 인젝션을 막기 위해, 특수문자는 `~!@#%^*`만 허용.
    public static final String PASSWORD_REG = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~!@#%^*]).{8,16}$";

    // 유저 닉네임 규칙: 소문자(a~z), 대문자(A~Z), 숫자(0~9), 한글(ㄱ~ㅎ, ㅏ~ㅣ, 가~힣)만 가능하며, 2~10자로 구성. 특수문자를 허용하지 않음.
    // 단, OAuth 회원가입 시 닉네임이 중복될 경우에만 '#'과 임의의 숫자를 이용함.
    public static final String NICKNAME_REG = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{2,10}$";
}
