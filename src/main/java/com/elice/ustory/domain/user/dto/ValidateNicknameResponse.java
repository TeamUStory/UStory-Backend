package com.elice.ustory.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateNicknameResponse {
    Boolean isValid;
    Boolean isDuplicate; //중복될 경우 true
}
