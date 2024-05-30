package com.elice.ustory.domain.notice.dto;

import com.elice.ustory.domain.notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class NoticeDTO {
    private String message;
    private int messageType;
    private long receiverId;

    public abstract void populateNotice(Notice notice);
}
