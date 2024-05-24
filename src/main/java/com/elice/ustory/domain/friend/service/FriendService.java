package com.elice.ustory.domain.friend.service;

import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.repository.FriendRepository;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final NoticeService noticeService;

    @Autowired
    public FriendService(FriendRepository friendRepository, UserRepository userRepository, @Lazy NoticeService noticeService) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.noticeService = noticeService;
    }


    /**
     * 사용자의 전체 친구 리스트를 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 목록
     */
    public List<Users> getAllFriends(Long userId) {
        List<Friend> friends = friendRepository.findAllByIdUserId(userId);
        List<Users> friendList = new ArrayList<>();
        for (Friend friend : friends) {
            Users user = userRepository.findById(friend.getId().getFriendId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend with ID" + friend.getId().getFriendId() + " not found"));

            friendList.add(user);
        }
        return friendList;
    }


    /**
     * 닉네임으로 사용자를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 목록
     */
    public Users searchUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with nickname: " + nickname));
    }


    /**
     * 닉네임으로 친구 추가 요청을 보냅니다.
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param nickname 친구 추가할 사용자의 닉네임
     * @return 성공적으로 알람이 전송되었는지 여부
     */
    public void sendFriendRequest(Long senderId, String nickname) {
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sender ID"));
        Users receiver = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receiver nickname"));

        FriendId friendId = new FriendId(senderId, receiver.getId());

        // 친구 요청이 이미 존재하지 않는 경우에만 추가
        if (!friendRepository.existsById(friendId)) {
            // 친구 요청 저장
            Friend friend = Friend.builder()
                                    .id(friendId)
                                    .invitedAt(LocalDateTime.now())
                                    .user(sender)
                                    .friendUser(receiver)
                                    .build();
            friendRepository.save(friend);
            // 알림 전송
            noticeService.sendFriendRequestNotice(senderId, receiver.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists");
        }
    }


    /**
     * 친구 요청을 수락하고 친구 관계를 추가합니다.(수락 후 알람 삭제)
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @return 성공적으로 친구가 추가되었는지 여부
     */
    public void acceptFriendRequest(Long senderId, Long receiverId) {
        FriendId friendId = new FriendId(receiverId, senderId);

        if (!friendRepository.existsById(friendId)) {
            Friend friend = Friend.builder()
                                    .id(friendId)
                                    .invitedAt(LocalDateTime.now())
                                    .acceptedAt(LocalDateTime.now())
                                    .build();
            friendRepository.save(friend);

            //알림삭제
            noticeService.deleteFriendRequestNotice(senderId, receiverId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already accepted or does not exist");
        }
    }



    /**
     * 친구 요청을 거절합니다.(알람 및 친구 요청 삭제)
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     */
    public void rejectFriendRequest(Long senderId, Long receiverId) {
        FriendId friendId = new FriendId(receiverId, senderId);

        // 친구 요청이 존재하지 않는 경우 예외를 던짐
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        //친구 요청 삭제
        friendRepository.delete(friend);

        //notice 삭제
        noticeService.deleteFriendRequestNotice(senderId, receiverId);
    }


    /**
     * 친구 관계를 삭제합니다.
     *
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     */
    public void deleteFriendById(Long userId, Long friendId) {
        FriendId id = new FriendId(userId, friendId);

        // 친구 관계가 존재하지 않는 경우 예외를 던짐
        if (!friendRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend relationship not found");
        }

        // 친구 관계 삭제
        friendRepository.deleteById(id);
    }



}
