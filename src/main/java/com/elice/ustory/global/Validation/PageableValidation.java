package com.elice.ustory.global.Validation;

import com.elice.ustory.global.exception.model.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageableValidation {

    private static final String AT_LEAST_PAGE = "페이지는 1 이상이어야 합니다.";
    private static final String AT_LEAST_SIZE = "사이즈는 1 이상이어야 합니다.";


    /**
     *
     * @param page
     * @param size
     * 페이지네이션에서 페이지와 사이즈가 1 이상이여야 함을 검증하는 메서드
     * Pageable 까지 만들어서 넘기는 버전, 그냥 검증한 하는 버전 두 가지 버전의 메서드를 구현
     * 하나로 만들면 좋았겠다. 하지만 서비스단까지 바꿔야 하는 관계로 패스
     */
    public static void pageValidate(int page, int size) {
        if (page < 1) {
            throw new ValidationException(AT_LEAST_PAGE);
        } else if (size < 1) {
            throw new ValidationException(AT_LEAST_SIZE);
        }
    }

    public static Pageable madePageable(int page, int size) {

        if (page < 1) {
            throw new ValidationException(AT_LEAST_PAGE);
        } else if (size < 1) {
            throw new ValidationException(AT_LEAST_SIZE);
        }

        return PageRequest.of(page - 1, size);
    }

}

