package com.elice.ustory.domain.paper.dto;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
@AllArgsConstructor
public class PaperCountRequest {

    // 필수요소, HTTP 메시지로 페이퍼 갯수에 대한 요청을 할 때 항상 존재해야하는 값이므로, final을 붙여서 불변으로 만든다.
    // 나머지 필드가 null 값인 생성자를 만들어서 필수요소만 들어올 시, 유저가 작성한 모든 페이퍼를 카운트 하는 로직을 짠다.
    // 마이 페이지에서 쓰인다.
    private final Long userId;

    // 선택요소, HTTP 메시지에서 들어오는 양식에 따라, null 값이 들어올 수 있기 때문에 final을 붙이지 않는다.
    // 이 경우로 생성자를 만들게 될 시에는, userId, diaryId 둘 다 이용하여 사용자가 속한 다이어리의 전체 페이지를 카운트 하는 로직을 짠다.
    private Long diaryId;

    // 현재 페이퍼의 갯수가 필요한 상황은 두 가지이다.
    // 메인화면에서 본인이 작성한 페이지 전체의 갯수와
    // 다이어리 상세 정보에 들어갔을 때, 해당 다이어리에 속해있는 페이퍼의 갯수를 프론트엔드에 제공하는 것이다.

    // TODO : 여기에 검증하는 메서드 등이 들어가야 하는가? 객체 지향으로 하려면 굳이 알고 있어야 하는가에 대한 질문
    // TODO : 라우터와 같은 역할을 하는 메서드에 대한 질문


}
