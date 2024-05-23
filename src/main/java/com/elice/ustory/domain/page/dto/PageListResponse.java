package com.elice.ustory.domain.page.dto;

import com.elice.ustory.domain.page.entity.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PageListResponse {

    @Schema(name = "페이지 타이틀")
    private String title;

    @Schema(name = "썸네일 이미지")
    private String thumbnailImage;

    @Schema(name = "다이어리 이름")
    private String diaryName;

    @Schema(name = "상호명")
    private String store;

    @Schema(name = "페이지 Id")
    private Long pageId;

    // TODO: 다이어리 엔티티 연동 후 주석 풀어야 함
    public PageListResponse(Page page) {
        this.title = page.getTitle();
        this.thumbnailImage = page.getThumbnailImage();
        this.diaryName = "다이어리 이름"; //page.getDiary().getTitle();
        this.store = page.getAddress().getStore();
        this.pageId = page.getId();
    }
}
