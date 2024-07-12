package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.constant.RegexPatterns;
import com.elice.ustory.domain.user.constant.UserMessageConstants;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ValidateNicknameRequest {
    @Pattern(regexp = RegexPatterns.NICKNAME_REG, message = UserMessageConstants.NOT_APPROPIRATE_NICKNAME_MSSAGE)
    private String nickname;
}
