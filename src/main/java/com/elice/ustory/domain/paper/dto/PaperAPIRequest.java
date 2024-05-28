package com.elice.ustory.domain.paper.dto;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

// @RequiredArgsConstructor
@AllArgsConstructor
public class PaperAPIRequest {
    // TODO : 근데 이거 생성자를 각자 만들어 줘야하는거에요? 아니면 생성자는 ALL 이지만, null 값을 서비스에서 체크하는 거에요?

    // 어떤 상황에서도 로그인이 된 상태로 제공하는 시스템이기 때문에 유저 아이디는 필수 정보로 가져올 수 있다.
    // 유저 아이디만 파라미터로 넘어오게 되는 경우에는 해당 유저가 작성한 모든 페이퍼를 리턴한다.
    // Main 화면에서는 6개씩 끊어서 페이지네이션 적용할 것
    private final Long userId;

    // 다이어리 아이디와 함께 넘어오는 경우에는 해당 유저가 속한 다이어리의 모든 페이퍼를 응답한다.
    // 최근 2개만 넘겨주면 된다.
    private Long diaryId;

    // 날짜가 두 가지로 넘어올 지, 하나로 묶여서 넘어올 지는 모르겠지만 유저와 다이어리 아이디, 날짜가 같이 넘어오는 경우에는
    // 날짜 사이에 작성된, 다이어리에 속한, 페이퍼들을 전부 반환한다. 무한 스크롤이며, 어떻게 끊어서 보낼지에 대해서는 의견 조율 필요
    private LocalDate startDate;
    private LocalDate endDate;

    // 페이퍼 아이디가 유저 아이디와 넘어오는 경우에는 해당 페이퍼 페이지를 보내주면 된다.
    // 이거 DTO 만들어서 넘겨 줄껀가? 리턴할때? 그냥 페이퍼로 보낼껀가?
    private Long paperId;

}
