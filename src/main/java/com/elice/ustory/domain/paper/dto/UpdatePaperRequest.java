package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

    @Schema(description = "페이퍼의 제목", example = "합정역 맛집 우규", required = true)
    @NotNull(message = "페이퍼의 제목은 필수입니다. 제목을 작성하여 주세요.")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://~~~~", required = true)
    @NotNull(message = "페이퍼의 썸네일은 필수입니다. 썸네일을 지정해 주세요.")
    private String thumbnailImageUrl;

    @Schema(description = "나머지 이미지 URL 리스트", example = "[\"https://~\", \"https://~\"]")
    private List<String> imageUrls;

    @Schema(description = "방문 날짜", example = "2024/05/23", required = true)
    @NotNull(message = "페이퍼의 방문일은 필수입니다. 방문일을 지정해 주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate visitedAt;

    @Schema(description = "다이어리 Id", example = "12345678", required = true)
    @NotNull(message = "다이어리 지정은 필수입니다. 다이어리를 지정해 주세요.")
    private Long diaryId;

    @Schema(description = "도로 주소", example = "서울특별시 마포구 독막로3길 21", required = true)
    @NotNull(message = "장소는 필수입니다. 장소를 지정해 주세요.")
    private String city;

    @Schema(description = "상호명", example = "우규", required = true)
    @NotNull(message = "상호명은 필수입니다. 상호명을 지정해 주세요.")
    private String store;

    @Schema(description = "X좌표", example = "37.5494", required = true)
    @NotNull(message = "좌표값은 필수입니다. 좌표를 지정해 주세요.")
    private Double coordinateX;

    @Schema(description = "Y좌표", example = "126.9169", required = true)
    @NotNull(message = "좌표값은 필수입니다. 좌표를 지정해 주세요.")
    private Double coordinateY;

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
