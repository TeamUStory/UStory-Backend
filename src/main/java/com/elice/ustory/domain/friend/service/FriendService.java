package com.elice.ustory.domain.friend.service;

import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.UserListDTO;
import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.friend.repository.FriendRepository;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.util.CommonUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
     * 닉네임으로 전체 사용자를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 목록 (옵셔널)
     */
    public Optional<UserListDTO> findUserByNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .filter(name -> !name.isEmpty())
                .flatMap(userRepository::findByNickname)
                .map(u -> UserListDTO.builder()
                        .name(u.getName())
                        .nickname(u.getNickname())
                        .profileImg(u.getProfileImg())
                        .build());
    }

    /**
     * 친구 추가 요청을 보냅니다.
     *
     * @param friendRequestDTO 친구 요청 정보
     */
    public void sendFriendRequest(FriendRequestDTO friendRequestDTO) {
        String senderNickname = friendRequestDTO.getSenderNickname();
        String receiverNickname = friendRequestDTO.getReceiverNickname();

        Users sender = userRepository.findByNickname(senderNickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        Users receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        FriendId friendId = CommonUtils.createFriendId(sender.getId(), receiver.getId());
        validateFriendRequestNotExists(friendId);

        Friend friend = CommonUtils.createFriendEntity(sender, receiver, friendId, FriendStatus.PENDING, LocalDateTime.now(), null);
        friendRepository.save(friend);

        FriendNoticeDTO noticeDTO = CommonUtils.createFriendNoticeDTO(receiver.getId(), sender.getId(), 1, sender.getNickname(), LocalDateTime.now(), null);
        noticeService.sendNotice(noticeDTO);
    }

    /**
     * 특정 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 요청 목록
     */
    public List<FriendRequestDTO> getFriendRequests(Long userId) {
        return friendRepository.findFriendRequests(userId);
    }

    /**
     * 친구 요청 존재 여부 확인
     *
     * @param friendId 친구 요청 ID
     */
    private void validateFriendRequestNotExists(FriendId friendId) {
        if (friendRepository.existsById(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists");
        }
    }

    /**
     * 사용자 조회
     *
     * @param userId 사용자 ID
     * @return Users 객체
     */
    private Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID"));
    }


    /**
     * 친구 요청에 응답합니다.
     *
     * @param senderNickname 친구 요청을 보낸 사용자의 닉네임
     * @param receiverNickname 친구 요청을 받은 사용자의 닉네임
     * @param accepted true이면 요청 수락, false이면 요청 거절
     */
    public void respondToFriendRequest(String senderNickname, String receiverNickname, boolean accepted) {
        Users sender = userRepository.findByNickname(senderNickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        Users receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        FriendId friendId = CommonUtils.createFriendId(sender.getId(), receiver.getId());

        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

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
        // 반대 방향 친구 관계도 추가 (sender와 receiver를 반대로 설정)
        Friend reverseFriend = CommonUtils.createFriendEntity(
                friend.getFriendUser(),
                friend.getUser(),
                CommonUtils.createFriendId(receiverId, senderId),
                FriendStatus.ACCEPTED,
                friend.getInvitedAt(),
                LocalDateTime.now()
        );
        friendRepository.save(reverseFriend);

        // 수락한 사람 (receiver)의 닉네임 조회
        String receiverNickname = getUserById(receiverId).getNickname();

        // 친구 수락 알림 전송
        FriendNoticeDTO noticeDTO = CommonUtils.createFriendNoticeDTO(senderId, receiverId, 3, receiverNickname, null, LocalDateTime.now());
        noticeService.sendNotice(noticeDTO);
    }


    /**
     * 친구 관계를 삭제합니다.
     *
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     */
    public void deleteFriendById(Long userId, Long friendId) {
        FriendId id = CommonUtils.createFriendId(userId, friendId);
        FriendId reverseId = CommonUtils.createFriendId(friendId, userId);

        if (!friendRepository.existsById(id) && !friendRepository.existsById(reverseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend relationship not found");
        }

        friendRepository.deleteById(id);
        friendRepository.deleteById(reverseId);
    }



}