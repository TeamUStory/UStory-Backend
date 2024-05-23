package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
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
public class UpdatePaperRequest {

    @Schema(name = "타이틀")
    private String title;

    @Schema(name = "썸네일 URL")
    private String thumbnailImage;

    @Schema(name = "이미지 URL")
    private List<String> images;

    @Schema(name = "방문 날짜")
    private LocalDate visitedAt;


    @Schema(name = "다이어리 Id")
    private Long diaryId;

    @Schema(name = "사용자 Id", description = "우선 사용자 Id를 기입했지만, 토큰 사용할 때 사라질 예정")
    private Long memberId;


    @Schema(name = "도로 주소")
    private String city;

    @Schema(name = "상세 주소")
    private String detail;

    @Schema(name = "상호명")
    private String store;

    @Schema(name = "X좌표")
    private float coordinateX;

    @Schema(name = "Y좌표")
    private float coordinateY;

    public Paper toPageEntity() {
        return Paper.createBuilder()
                .title(this.title)
                .thumbnailImage(this.thumbnailImage)
                .visitedAt(this.visitedAt)
                .build();
    }

    public Address toAddressEntity() {
        return Address.createBuilder()
                .city(this.city)
                .detail(this.detail)
                .store(this.store)
                .coordinateX(this.coordinateX)
                .coordinateY(this.coordinateY)
                .build();
    }

    public List<Image> toImagesEntity() {

        List<Image> images = new ArrayList<>();

        for (String imageUrl : this.images) {
            images.add(new Image(imageUrl));
        }

        return images;
    }
}
