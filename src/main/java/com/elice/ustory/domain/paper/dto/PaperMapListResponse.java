package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PaperMapListResponse {

    @Schema(description = "PaperId", example = "123456")
    private Long paperId;

    @Schema(description = "타이틀", example = "여기 놀러갔었어요")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://~~~~")
    private String thumbnailImageUrl;


    @Schema(description = "상호명", example = "우규")
    private String store;

    @Schema(description = "X좌표", example = "37.5494")
    private Double coordinateX;

    @Schema(description = "Y좌표", example = "126.9169")
    private Double coordinateY;


    @Schema(description = "다이어리 색상", example = "#FDD0B1")
    private String diaryColor;

    @Schema(description = "다이어리 이미지 URL", example = "https://~~~~")
    private String diaryImageUrl;

    @Schema(description = "다이어리 마커 URL", example = "https://ustory-bucket.s3.ap-northeast-2.amazonaws.com/common/black-marker.png")
    private String diaryMarkerUrl;

    public PaperMapListResponse(Paper paper) {
        this.paperId = paper.getId();
        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.store = paper.getAddress().getStore();
        this.coordinateX = paper.getAddress().getCoordinateX();
        this.coordinateY = paper.getAddress().getCoordinateY();
        this.diaryColor = paper.getDiary().getColor().getHexCode();
        this.diaryImageUrl = paper.getDiary().getImgUrl();
        this.diaryMarkerUrl = paper.getDiary().getColor().getMarkerUrl();
    }
}