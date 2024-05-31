package com.elice.ustory.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateNicknameResponse {
    Boolean isDuplicate; //중복될 경우 true
    Boolean isInappropriate; //조건에 부적절할 경우 true
}
