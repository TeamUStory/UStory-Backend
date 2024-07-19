package com.elice.ustory.domain.recommand.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecommendPaperRequest {

    private List<Long> paperIds;

}
