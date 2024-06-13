package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.entity.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateRequest {
    private String name;
    private String nickname;
    private String profileImgUrl;
    private String profileDescription;
}
