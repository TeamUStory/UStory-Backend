package com.elice.ustory.domain.recommand.dto;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.address.AddressRecommendDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendRedisDTO {

    AddressRecommendDTO addressRecommendDTO;
    List<Long> paperIds;

}
