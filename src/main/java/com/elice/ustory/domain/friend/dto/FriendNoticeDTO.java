//package com.elice.ustory.domain.friend.dto;
//
//
//import com.elice.ustory.domain.notice.dto.NoticeDTO;
//import com.elice.ustory.domain.notice.entity.Notice;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Getter
//@NoArgsConstructor
//public class FriendNoticeDTO extends NoticeDTO {
//    private long senderId;
//    private String nickname;
//    private LocalDateTime invitedAt;
//    private LocalDateTime acceptedAt;
//
//    @Builder
//    public FriendNoticeDTO(String message, int messageType, long receiverId, long senderId, String nickname,LocalDateTime invitedAt, LocalDateTime acceptedAt) {
//        super(message, messageType, receiverId);
//        this.senderId = senderId;
//        this.nickname = nickname;
//        this.invitedAt = invitedAt;
//        this.acceptedAt = acceptedAt;
//    }
//
////    @Override
////    public void populateNotice(Notice notice) {
////        notice.setSenderId(this.senderId);
////    }
//}