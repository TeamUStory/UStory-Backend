package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Paper;

import java.time.LocalDate;

public class PaperCardResponse {
    private String title;
    private String thumbNailImageUrl;
    private LocalDate visitedAt;
    private String storeName;

    // Paper 객체를 DTO로 변환시키는 과정, API로 들어온 것들에서는 카드 형식으로 주는 형태 밖에 없어서 모든 응답은 아마 이렇게 갈 것 같다.
    // TODO: 질문, 이걸 생성자로 하는게 맞는지 아니면 메서드로 따로 빼는게 맞는지. 배울 때는 메서드로 뺐던것 같은데 두번 할 필요가 있나?
    PaperCardResponse(Paper paper) {
        this.title = paper.getTitle();
        this.thumbNailImageUrl = paper.getThumbnailImage();
        this.visitedAt = paper.getVisitedAt();
        this.storeName = paper.getAddress().getStore();
    }
}
