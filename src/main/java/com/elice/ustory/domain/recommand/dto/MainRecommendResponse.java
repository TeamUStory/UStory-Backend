package com.elice.ustory.domain.recommand.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainRecommendResponse {

    private String store;
    private String imgUrl;
    private List<Long> paperIds;

}
