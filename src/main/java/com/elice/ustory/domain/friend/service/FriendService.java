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
        Users sender = userRepository.findById(friendRequestDTO.getSenderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sender ID"));
        Users receiver = userRepository.findById(friendRequestDTO.getReceiverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receiver ID"));

        FriendId friendId = new FriendId(friendRequestDTO.getSenderId(), friendRequestDTO.getReceiverId());

        if (friendRepository.existsById(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists");
        }

        Friend friend = Friend.builder()
                .id(friendId)
                .invitedAt(LocalDateTime.now())
                .user(sender)
                .friendUser(receiver)
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);

        FriendNoticeDTO noticeDTO = FriendNoticeDTO.builder()
                .receiverId(friendRequestDTO.getReceiverId())
                .senderId(friendRequestDTO.getSenderId())
                .messageType(1)
                .invitedAt(LocalDateTime.now())
                .nickname(sender.getNickname())
                .build();

        System.out.println("FriendNoticeDTO before sending notice: " + noticeDTO);

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
        FriendId friendId = new FriendId(senderId, receiverId);

        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        if (accepted) {
            // 친구 요청을 수락하는 경우
            friend.updateStatus(FriendStatus.ACCEPTED);
            // 반대 방향 친구 관계도 추가
            FriendId reverseFriendId = new FriendId(receiverId, senderId);
            Friend reverseFriend = Friend.builder()
                    .id(reverseFriendId)
                    .invitedAt(friend.getInvitedAt())
                    .acceptedAt(LocalDateTime.now())
                    .user(friend.getFriendUser())
                    .friendUser(friend.getUser())
                    .status(FriendStatus.ACCEPTED)
                    .build();
            friendRepository.save(reverseFriend);

            // 수락한 사람 (receiver)의 닉네임 조회
            Users receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receiver ID"));
            String receiverNickname = receiver.getNickname();

            // 친구 수락 알림 전송
            FriendNoticeDTO noticeDTO = FriendNoticeDTO.builder()
                    .receiverId(senderId)
                    .senderId(receiverId)
                    .messageType(3)
                    .acceptedAt(LocalDateTime.now())
                    .nickname(receiverNickname)
                    .build();
            noticeService.sendNotice(noticeDTO);
        } else {
            // 친구 요청을 거절하는 경우
            friendRepository.delete(friend);
        }

        // 친구 요청 알림 삭제
        noticeService.deleteNoticeBySender(senderId, receiverId, 1);
    }



    /**
     * 친구 관계를 삭제합니다.
     *
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     */
    public void deleteFriendById(Long userId, Long friendId) {
        FriendId id = new FriendId(userId, friendId);
        FriendId reverseId = new FriendId(friendId, userId);

        // 친구 관계가 존재하지 않는 경우 예외를 던짐
        if (!friendRepository.existsById(id) && !friendRepository.existsById(reverseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend relationship not found");
        }

        // 친구 관계 삭제
        friendRepository.deleteById(id);
        friendRepository.deleteById(reverseId);
    }



}
