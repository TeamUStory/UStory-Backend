package com.elice.ustory.domain.address;

import com.elice.ustory.domain.recommand.dto.RecommendCountDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AddressQueryDslRepository {

    List<RecommendCountDTO> countEqualAddress();
    List<RecommendCountDTO> countEqualAddress(Pageable pageable, LocalDateTime requestTime);
}
