package com.elice.ustory.domain.page.dto;

import com.elice.ustory.domain.page.entity.Image;
import com.elice.ustory.domain.page.entity.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PageResponse {

    @Schema(name = "페이지 이름")
    private String title;

    @Schema(name = "썸네일 URL")
    private String thumbnailImage;

    @Schema(name = "이미지 URL")
    private List<String> images;

    @Schema(name = "방문 날짜")
    private LocalDate visitedAt;

    @Schema(name = "도로 주소")
    private String city;

    @Schema(name = "상세 주소")
    private String detail;

    @Schema(name = "상호명")
    private String store;

    public PageResponse(Page page) {
        this.title = page.getTitle();
        this.thumbnailImage = page.getThumbnailImage();
        this.images = page.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        this.visitedAt = page.getVisitedAt();
        this.city = page.getAddress().getCity();
        this.detail = page.getAddress().getDetail();
        this.store = page.getAddress().getStore();
    }
}
