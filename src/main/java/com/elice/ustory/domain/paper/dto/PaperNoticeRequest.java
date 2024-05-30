package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaperNoticeRequest extends NoticeDTO {

    private Users receiver;
    private Paper paper;

    @Builder
    public PaperNoticeRequest(String message, int messageType, Long receiverId, Paper paper) {
        super(message, messageType, receiverId);
        this.paper = paper;
    }
    // 필요한 값이 뭐가 있지?
    @Override
    public void populateNotice(Notice notice) {
    }
}
