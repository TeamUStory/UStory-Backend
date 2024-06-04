package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPaperRequest {

    @Schema(description = "타이틀", example = "합정역 맛집 우규")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://~~~~")
    private String thumbnailImageUrl;

    @Schema(description = "나머지 이미지 URL 리스트", example = "[\"https://~\", \"https://~\"]")
    private List<String> imageUrls;

    @Schema(description = "방문 날짜", example = "2024/05/23")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate visitedAt;

    @Schema(description = "다이어리 Id", example = "12345678")
    private Long diaryId;

    @Schema(description = "도로 주소", example = "서울특별시 마포구 독막로3길 21")
    private String city;

    @Schema(description = "상호명", example = "우규")
    private String store;

    @Schema(description = "X좌표", example = "37.5494")
    private double coordinateX;

    @Schema(description = "Y좌표", example = "126.9169")
    private double coordinateY;

    @Schema(description = "작성자 Comment", example = "맛있는듯?")
    private String writerComment;

    public Paper toPageEntity() {
        return Paper.createBuilder()
                .title(this.title)
                .thumbnailImageUrl(this.thumbnailImageUrl)
                .visitedAt(this.visitedAt)
                .build();
    }

    public Address toAddressEntity() {
        return Address.createBuilder()
                .city(this.city)
                .store(this.store)
                .coordinateX(this.coordinateX)
                .coordinateY(this.coordinateY)
                .build();
    }

    public List<Image> toImagesEntity() {
        List<Image> images = new ArrayList<>();

        int count = 1;
        for (String imageUrl : this.imageUrls) {
            images.add(new Image(imageUrl, count++));
        }

        return images;
    }
}
