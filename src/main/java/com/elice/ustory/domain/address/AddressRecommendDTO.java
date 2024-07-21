package com.elice.ustory.domain.address;

import com.elice.ustory.domain.recommand.dto.RecommendCountDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRecommendDTO {

    private String store;
    private String city;
    private Double coordinateX;
    private Double coordinateY;

    public AddressRecommendDTO(RecommendCountDTO recommendCountDTO) {
        this.store = recommendCountDTO.getStore();
        this.city = recommendCountDTO.getCity();
        this.coordinateX = recommendCountDTO.getCoordinateX();
        this.coordinateY = recommendCountDTO.getCoordinateY();
    }
}
