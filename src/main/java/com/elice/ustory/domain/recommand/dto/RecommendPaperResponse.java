package com.elice.ustory.domain.recommand.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendPaperResponse {

    private String store;

    private List<RecommendPaperDTO> recommendPaper;

}
