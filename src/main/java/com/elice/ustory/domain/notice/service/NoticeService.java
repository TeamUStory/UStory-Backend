package com.elice.ustory.domain.notice.service;

import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.notice.entity.MessageType;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private FriendService friendService;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, UserRepository userRepository, @Lazy FriendService friendService) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.friendService = friendService;
    }


    /**
     * 특정 사용자의 모든 알림을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 알림 목록
     */
    public List<Notice> getAllNoticesByUserId(Long userId) {
        // 사용자가 존재하지 않는 경우 예외를 던짐
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // 알림 목록 조회
        return noticeRepository.findByReceiverId(userId);
    }


    /**
     * 친구 요청 알람을 전송합니다.
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     */
    public void sendFriendRequestNotice(Long senderId, Long receiverId) {
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        Users receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "receiver not found"));
        String message = MessageType.Friend.createMessage(sender.getNickname());
        Notice notice = new Notice();
        notice.setReceiverId(receiverId);
        notice.setSenderId(senderId);
        notice.setMessage(message);
        notice.setMessageType(MessageType.Friend);
        noticeRepository.save(notice);
    }


    /**
     * 친구 요청 알람에 응답합니다.
     *
     * @param noticeId 알람 ID
     * @param accepted true이면 수락, false이면 거절
     */
    public void respondToFriendRequest(Long noticeId, boolean accepted) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));

        if (notice.getMessageType() != MessageType.Friend) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message type");
        }

        Long senderId = notice.getSenderId();
        Long receiverId = notice.getReceiverId();

        if (accepted) {
            friendService.acceptFriendRequest(senderId, receiverId);
        } else {
            friendService.rejectFriendRequest(senderId, receiverId);
        }
    }


    /**
     * 친구 요청 알람을 삭제합니다.
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     */
    public void deleteFriendRequestNotice(Long senderId, Long receiverId) {
        Optional<Notice> noticeOptional = Optional.ofNullable(noticeRepository.findBySenderIdAndReceiverIdAndMessageType(senderId, receiverId, MessageType.Friend));
        noticeOptional.ifPresent(notice -> noticeRepository.delete(notice));
    }


}
