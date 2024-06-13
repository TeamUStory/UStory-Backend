package com.elice.ustory.domain.user.entity;

public class RegexPatterns {
    public static final String EMAIL_REG = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";


    // 유저 비밀번호 규칙: 숫자, 영문, 특수문자 각 1개를 포함한 8~16자. 보안상 SQL 인젝션을 막기 위해, 특수문자는 `~!@#%^*`만 허용.
    public static final String PASSWORD_REG = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~!@#%^*]).{8,16}$";
}
