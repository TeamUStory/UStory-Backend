package com.elice.ustory.global.util;

import com.elice.ustory.domain.friend.dto.FriendNoticeDTO;
import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.friend.repository.FriendRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


public class FriendRequestUtils {

    /**
     * 친구 요청 ID 생성
     *
     * @param senderId   친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @return FriendId 객체
     */
    public static FriendId createFriendId(Long senderId, Long receiverId) {
        return new FriendId(senderId, receiverId);
    }


    /**
     * 친구 엔티티 생성
     *
     * @param sender    친구 요청을 보낸 사용자
     * @param receiver  친구 요청을 받은 사용자
     * @param friendId  친구 요청 ID
     * @param status    친구 요청 상태
     * @param invitedAt 친구 요청 시간
     * @param acceptedAt 친구 수락 시간
     * @return Friend 객체
     */
    public static Friend createFriendEntity(Users sender, Users receiver, FriendId friendId, FriendStatus status, LocalDateTime invitedAt, LocalDateTime acceptedAt) {
        return Friend.builder()
                .id(friendId)
                .invitedAt(invitedAt)
                .acceptedAt(acceptedAt)
                .user(sender)
                .friendUser(receiver)
                .status(status)
                .build();
    }

    /**
     * 친구 요청 존재 여부 확인
     *
     * @param friendRepository 친구 저장소
     * @param friendId 친구 요청 ID
     */
    public static void validateFriendRequestNotExists(FriendRepository friendRepository, FriendId friendId) {
        if (friendRepository.existsById(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists");
        }
    }

    /**
     * 사용자 조회
     *
     * @param userRepository 사용자 저장소
     * @param userId 사용자 ID
     * @return Users 객체
     */
    public static Users getUserById(UserRepository userRepository, Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID"));
    }


    /**
     * FriendNoticeDTO 생성 메서드
     *
     * @param receiverId 수신자의 ID
     * @param senderId 발신자의 ID
     * @param messageType 메시지 타입
     * @param nickname 닉네임
     * @param invitedAt 초대 시간
     * @param acceptedAt 수락 시간
     * @return FriendNoticeDTO 객체
     */
    public static FriendNoticeDTO createFriendNoticeDTO(Long receiverId, Long senderId, int messageType, String nickname, LocalDateTime invitedAt, LocalDateTime acceptedAt) {
        return FriendNoticeDTO.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .messageType(messageType)
                .invitedAt(invitedAt)
                .acceptedAt(acceptedAt)
                .nickname(nickname)
                .build();
    }




}
