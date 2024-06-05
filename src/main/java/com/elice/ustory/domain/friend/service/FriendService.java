package com.elice.ustory.domain.friend.service;

import com.elice.ustory.domain.friend.dto.FriendRequestListDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.friend.repository.FriendRepository;
import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.ConflictException;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.ValidationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final NoticeService noticeService;

    @Autowired
    public FriendService(FriendRepository friendRepository, UserRepository userRepository, @Lazy NoticeService noticeService, NoticeRepository noticeRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.noticeService = noticeService;
    }

    /**
     * 친구 요청 ID 생성
     *
     * @param senderId   친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @return FriendId 객체
     */
    private FriendId createFriendId(Long senderId, Long receiverId) {
        return new FriendId(senderId, receiverId);
    }

    /**
     * 두 사용자 간의 친구 요청을 나타내는 Friend 객체를 생성합니다.
     *
     * @param sender 친구 요청을 보낸 사용자
     * @param receiver 친구 요청을 받을 사용자
     * @return 생성된 Friend 객체
     */
    private Friend createFriend(Users sender, Users receiver) {
        return Friend.builder()
                .id(new FriendId(sender.getId(), receiver.getId()))
                .invitedAt(LocalDateTime.now())
                .status(FriendStatus.PENDING)
                .user(sender)
                .friendUser(receiver)
                .build();
    }

    /**
     * 사용자 조회
     *
     * @param userId 사용자 ID
     * @return Users 객체
     */
    private Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("잘못된 사용자 ID"));
    }


    /**
     * 사용자의 전체 친구 리스트를 조회하거나 닉네임으로 친구를 검색합니다.
     *
     * @param userId   조회할 사용자의 ID (옵션)
     * @param nickname 검색할 닉네임 (옵션)
     * @return 친구 목록 또는 검색된 친구 목록
     */
    public List<UserFriendDTO> getFriends(Long userId, String nickname) {
        return friendRepository.findFriends(userId, nickname);
    }

    /**
     * 친구 추가 요청을 보냅니다.
     *
     * @param userId 요청을 보낸 사용자의 ID
     * @param receiverNickname 친구 요청을 받을 사용자의 닉네임
     */
    // userid랑 ReceiverNickname으로 해결 해야됨
    public void sendFriendRequest(Long userId, String receiverNickname) {
        Users sender = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Sender를 찾을 수 없습니다."));
        Users receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new NotFoundException("Receiver를 찾을 수 없습니다."));

        FriendId friendId = createFriendId(sender.getId(), receiver.getId());

        // 이미 친구 요청이 있는지 확인
        validateFriendRequestNotExists(sender.getId(), receiver.getId());
        validateFriendRequestNotExists(receiver.getId(), sender.getId());


        // 친구 목록에 이미 있는 친구인지 확인
        validateNotAlreadyFriends(sender.getId(), receiver.getId());

        Friend friend = createFriend(sender, receiver);
        friendRepository.save(friend);

        NoticeRequest noticeRequest = NoticeRequest.builder()
                .responseId(receiver.getId())
                .senderId(sender.getId())
                .messageType(1)
                .build();

        noticeService.sendNotice(noticeRequest);
    }

    /**
     * 특정 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 요청 목록
     */
    public List<FriendRequestListDTO> getFriendRequests(Long userId) {
        return friendRepository.findFriendRequests(userId);
    }

    /**
     * 친구 요청 존재 여부 확인
     */
    private void validateFriendRequestNotExists(Long senderId, Long receiverId) {
        if (friendRepository.existsBySenderAndReceiverAndStatus(senderId, receiverId, FriendStatus.PENDING)) {
            throw new ConflictException("친구 요청이 이미 있습니다.");
        }
    }

    /**
     * 친구 존재 여부 확인
     */
    private void validateNotAlreadyFriends(Long userId, Long friendId) {
        if (friendRepository.existsBySenderAndReceiver(userId, friendId) ||
                friendRepository.existsBySenderAndReceiver(friendId, userId)) {
            throw new ConflictException("이미 친구로 등록되어 있습니다.");
        }
    }

    /**
     * 친구 요청에 응답합니다.
     *
     * @param userId 친구 요청을 받은 사용자의 ID
     * @param senderNickname 친구 요청을 보낸 사용자의 닉네임
     * @param accepted true이면 요청 수락, false이면 요청 거절
     */
    // 응답도 userId, senderNickname 으로 해서 보내야됨
    public void respondToFriendRequest(Long userId, String senderNickname, boolean accepted) {
        Users sender = userRepository.findByNickname(senderNickname)
                .orElseThrow(() -> new NotFoundException("sender를 찾을 수 없습니다."));
        Users receiver = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("receiver를 찾을 수 없습니다."));

        FriendId friendId = createFriendId(sender.getId(), receiver.getId());

        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("친구 요청을 찾을 수 없습니다."));

        if (accepted) {
            processAcceptedFriendRequest(friend, sender.getId(), receiver.getId());
        } else {
            friendRepository.delete(friend);
        }

        noticeService.deleteNoticeBySender(sender.getId(), receiver.getId(), 1);
    }


    /**
     * 친구 요청 수락 처리 로직
     *
     * @param friend Friend 객체 (기존 친구 관계 엔티티)
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     */
    private void processAcceptedFriendRequest(Friend friend, Long senderId, Long receiverId) {
        // 기존 친구 관계의 상태를 ACCEPTED로 업데이트
        friend.updateStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friend);


        // 반대 방향 친구 관계도 추가 (sender와 receiver를 반대로 설정)
        Users sender = getUserById(senderId);
        Users receiver = getUserById(receiverId);
        FriendId reverseFriendId = new FriendId(receiverId, senderId);

        Friend reverseFriend = Friend.builder()
                .id(reverseFriendId)
                .invitedAt(friend.getInvitedAt())
                .acceptedAt(LocalDateTime.now())
                .user(receiver)
                .friendUser(sender)
                .status(FriendStatus.ACCEPTED)
                .build();
        friendRepository.save(reverseFriend);

        NoticeRequest noticeRequest = NoticeRequest.builder()
                .responseId(senderId)
                .senderId(receiverId)
                .messageType(3)
                .build();

        noticeService.sendNotice(noticeRequest);

    }


    /**
     * 친구 관계를 삭제합니다.
     *
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     */
    public void deleteFriendById(Long userId, Long friendId) {
        FriendId id = createFriendId(userId, friendId);
        FriendId reverseId = createFriendId(friendId, userId);

        if (!friendRepository.existsById(id) && !friendRepository.existsById(reverseId)) {
            throw new NotFoundException("친구 관계를 찾을 수 없습니다");
        }

        friendRepository.deleteById(id);
        friendRepository.deleteById(reverseId);
    }



}