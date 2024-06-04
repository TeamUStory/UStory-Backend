package com.elice.ustory.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
    Boolean success;
}
