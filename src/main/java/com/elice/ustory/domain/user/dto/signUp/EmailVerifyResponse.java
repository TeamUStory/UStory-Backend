package com.elice.ustory.domain.user.dto.signUp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailVerifyResponse {
    Boolean isSuccess;
    String status;
}
