package com.elice.ustory.global.util;

import com.elice.ustory.domain.user.constant.RegexPatterns;
import com.elice.ustory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {
    private final UserRepository userRepository;

    private static final int NICKNAME_MAX_LENGTH = RegexPatterns.NICKNAME_MAX_LENGTH;
    public static final String SEPARATOR = "#"; // 닉네임과 임의값 사이의 구분자

    private static final int POSTFIX_LENGTH = 2;
    private static final int POSTFIX_LENGTH_WITH_SEPARATOR = POSTFIX_LENGTH + 1;

    public String formatNickname(String nickname) {
        // 닉네임에 포함된 특수문자를 제거한다.
        String formattedNickname = normalizeNicknameForOAuth(nickname);
        // 닉네임을 규정된 길이에 맞게 자른다.
        formattedNickname = trimNicknameForOAuth(nickname);
        // 중복 여부를 조회한다. 중복이면 닉네임을 7자 이내로 자르고, 겹치지 않을 때까지 임의의 postfix 3글자를 만들어 붙인다.
        if (checkDuplicateNickname(nickname)) {
            formattedNickname = trimNicknameForPostfix(formattedNickname);

            do {
                formattedNickname = formattedNickname + SEPARATOR + generateRandomPostfix();
            } while (checkDuplicateNickname(formattedNickname));
        }

        return formattedNickname;
    }

    public String generateRandomPostfix() {
        int leftLimit = 48; // 숫자 '0'의 ASCII 코드
        int rightLimit = 122; // 알파벳 'z'의 ASCII 코드
        int postfixLength = POSTFIX_LENGTH;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1) // leftLimit(포함) 부터 rightLimit+1(불포함) 사이의 난수 스트림 생성
                .filter(i -> (i < 57 || i >= 65) && ( i <= 90 || i >= 97)) // ASCII 테이블에서 숫자, 대문자, 소문자만 사용함
                .limit(postfixLength) // 생성된 난수를 지정된 길이로 잘라냄
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // 생성된 난수를 ASCII 테이블에서 대응되는 문자로 변환
                .toString(); // StringBuilder 객체를 문자열로 변환해 반환
    }

    public String normalizeNicknameForOAuth(String socialNickname) {
        // 정규식에 맞지 않는 글자를 제외
        String cleanedNickname = socialNickname.replaceAll(RegexPatterns.NICKNAME_REG_LETTER_FOR_REPLACE, "");
        return cleanedNickname;
    }

    public String 임의의값을_추가하는_함수(String cleanedNickname) {
        //TODO
        return "String";
    }

    public String trimNicknameForOAuth(String cleanedNickname) {
        if (cleanedNickname == null) {
            return null;
        }
        String trimmedNickname = cleanedNickname.length() > NICKNAME_MAX_LENGTH ? cleanedNickname.substring(0, NICKNAME_MAX_LENGTH) : cleanedNickname;
        return trimmedNickname;
    }

    public String trimNicknameForPostfix(String nickname) {
        if (nickname == null) {
            return null;
        }
        int availableLength = NICKNAME_MAX_LENGTH - POSTFIX_LENGTH_WITH_SEPARATOR;
        String trimmedNickname = nickname.length() > (availableLength) ? nickname.substring(0, availableLength) : nickname;
        return trimmedNickname;
    }

    public boolean checkDuplicateNickname(String nickname) {
        if (userRepository.countByNicknameWithSoftDeleted(nickname) > 0) {
            return true;
        } else {
            return false;
        }
    }
}
