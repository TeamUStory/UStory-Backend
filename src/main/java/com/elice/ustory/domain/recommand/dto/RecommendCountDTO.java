package com.elice.ustory.domain.recommand.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RecommendCountDTO {

    private String store;
    private String city;
    private Double coordinateX;
    private Double coordinateY;
    private Long count;

}
