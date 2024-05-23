package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PaperResponse {

    @Schema(name = "타이틀")
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

    public PaperResponse(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImage = paper.getThumbnailImage();
        this.images = paper.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        this.visitedAt = paper.getVisitedAt();
        this.city = paper.getAddress().getCity();
        this.detail = paper.getAddress().getDetail();
        this.store = paper.getAddress().getStore();
    }
}
