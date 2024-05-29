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
import com.elice.ustory.global.util.FriendRequestUtils;
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
     * @return 검색된 사용자 목록
     */
    public UserListDTO findAllUsersByNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .filter(name -> !name.isEmpty())
                .flatMap(name -> userRepository.findByNickname(name)
                        .map(u -> UserListDTO.builder()
                                .name(u.getName())
                                .nickname(u.getNickname())
                                .profileImg(u.getProfileImg())
                                .build()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nickname cannot be null, empty, or not found"));
    }


    /**
     * 친구 추가 요청을 보냅니다.
     *
     * @param friendRequestDTO 친구 요청 정보
     */
    public void sendFriendRequest(FriendRequestDTO friendRequestDTO) {
        Users sender = FriendRequestUtils.getUserById(userRepository, friendRequestDTO.getSenderId());
        Users receiver = FriendRequestUtils.getUserById(userRepository, friendRequestDTO.getReceiverId());

        FriendId friendId = FriendRequestUtils.createFriendId(sender.getId(), receiver.getId());
        FriendRequestUtils.validateFriendRequestNotExists(friendRepository, friendId);

        Friend friend = FriendRequestUtils.createFriendEntity(sender, receiver, friendId, FriendStatus.PENDING, LocalDateTime.now(), null);
        friendRepository.save(friend);

        FriendNoticeDTO noticeDTO = FriendRequestUtils.createFriendNoticeDTO(receiver.getId(), sender.getId(), 1, sender.getNickname(), LocalDateTime.now(), null);
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
     * 친구 요청에 응답합니다.
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @param accepted true이면 요청 수락, false이면 요청 거절
     */
    public void respondToFriendRequest(Long senderId, Long receiverId, boolean accepted) {
        FriendId friendId = FriendRequestUtils.createFriendId(senderId, receiverId);

        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        if (accepted) {
            processAcceptedFriendRequest(friend, senderId, receiverId);
        } else {
            friendRepository.delete(friend);
        }

        noticeService.deleteNoticeBySender(senderId, receiverId, 1);
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
        Friend reverseFriend = FriendRequestUtils.createFriendEntity(
                friend.getFriendUser(),
                friend.getUser(),
                FriendRequestUtils.createFriendId(receiverId, senderId),
                FriendStatus.ACCEPTED,
                friend.getInvitedAt(),
                LocalDateTime.now()
        );
        friendRepository.save(reverseFriend);

        // 수락한 사람 (receiver)의 닉네임 조회
        String receiverNickname = FriendRequestUtils.getUserById(userRepository, receiverId).getNickname();
        // 친구 수락 알림 전송
        FriendNoticeDTO noticeDTO = FriendRequestUtils.createFriendNoticeDTO(senderId, receiverId, 3, receiverNickname, null, LocalDateTime.now());

        noticeService.sendNotice(noticeDTO);
    }


    /**
     * 친구 관계를 삭제합니다.
     *
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     */
    public void deleteFriendById(Long userId, Long friendId) {
        FriendId id = FriendRequestUtils.createFriendId(userId, friendId);
        FriendId reverseId = FriendRequestUtils.createFriendId(friendId, userId);

        if (!friendRepository.existsById(id) && !friendRepository.existsById(reverseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend relationship not found");
        }

        friendRepository.deleteById(id);
        friendRepository.deleteById(reverseId);
    }



}
