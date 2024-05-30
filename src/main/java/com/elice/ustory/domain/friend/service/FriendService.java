package com.elice.ustory.domain.friend.service;

import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.UserListDTO;
import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.friend.repository.FriendRepository;
import com.elice.ustory.domain.notice.entity.MessageType;
import com.elice.ustory.domain.notice.entity.Notice;
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
import java.util.stream.Collectors;

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
        if (userId != null) {
            List<UserFriendDTO> friends = Optional.ofNullable(nickname)
                    .filter(name -> !name.isEmpty())
                    .map(name -> friendRepository.findFriendsByUserIdAndNickname(userId, name))
                    .orElseGet(() -> friendRepository.findAllFriendsByUserId(userId));

            if (friends.isEmpty() && nickname != null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No friends found with nickname " + nickname);
            }
            return friends;
        } else if (nickname != null && !nickname.isEmpty()) {
            Users user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with nickname " + nickname + " not found"));

            List<UserFriendDTO> friends = friendRepository.findFriendsByUserIdAndNickname(user.getId(), nickname);
            if (friends.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No friends found with nickname " + nickname);
            }
            return friends;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either userId or nickname must be provided");
        }
    }


    /**
     * 닉네임으로 전체 사용자를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 목록
     */
    public List<UserListDTO> findAllUsersByNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nickname cannot be null or empty");
        }

        List<UserListDTO> users = userRepository.findByNicknameContaining(nickname)
                .stream()
                .map(u -> UserListDTO.builder()
                        .userId(u.getId())
                        .email(u.getEmail())
                        .name(u.getName())
                        .nickname(u.getNickname())
                        .profileImg(u.getProfileImgUrl())
                        .build())
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found with nickname " + nickname);
        }

        return users;
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

        // 친구 요청 저장 (임시)
        Friend friend = Friend.builder()
                .id(friendId)
                .invitedAt(LocalDateTime.now())
                .user(sender)
                .friendUser(receiver)
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);

        // 알림 전송
        noticeService.sendFriendRequestNotice(friendRequestDTO.getSenderId(), friendRequestDTO.getReceiverId());
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
        } else {
            // 친구 요청을 거절하는 경우
            friendRepository.delete(friend);
            // 알림 삭제
            noticeService.deleteFriendRequestNotice(senderId, receiverId);
            return;
        }

        friendRepository.save(friend);
        // 알림 삭제
        noticeService.deleteFriendRequestNotice(senderId, receiverId);
    }


    /**
     * 알림 ID를 이용해 친구 요청에 응답합니다.
     *
     * @param noticeId 알림 ID
     * @param accepted true이면 요청 수락, false이면 요청 거절
     */
    public void respondToFriendRequest(Long noticeId, boolean accepted) {
        Notice notice = noticeService.findById(noticeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));

        if (notice.getMessageType() != MessageType.Friend) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message type");
        }

        Long senderId = notice.getSenderId();
        Long receiverId = notice.getReceiverId();

        respondToFriendRequest(senderId, receiverId, accepted);
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
