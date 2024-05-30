package com.elice.ustory.domain.friend.controller;

import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.UserListDTO;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Tag(name = "friend", description = "Friend API")
@RestController
@RequestMapping("api/friend")
public class FriendController {

    private FriendService friendService;
    private NoticeService noticeService;

    private UserRepository userRepository;

    public FriendController(FriendService friendService, NoticeService noticeService, UserRepository userRepository) {
        this.friendService = friendService;
        this.noticeService = noticeService;
        this.userRepository = userRepository;
    }

    /**
     * 사용자의 전체 친구 리스트를 조회하거나 닉네임으로 친구를 검색합니다.
     *
     * @param userId   조회할 사용자의 ID (옵션)
     * @param nickname 검색할 닉네임 (옵션)
     * @return 친구 목록 또는 검색된 친구 목록
     */
    @Operation(summary = "Get / Friends", description = "사용자의 전체 친구 리스트를 조회하거나 닉네임으로 친구를 검색합니다.")
    @GetMapping("/friends")
    public ResponseEntity<List<UserFriendDTO>> getFriends(@RequestParam(required = false) Long userId, @RequestParam(required = false) String nickname) {
        List<UserFriendDTO> friends = friendService.getFriends(userId, nickname);
        return ResponseEntity.ok(friends);
    }


    /**
     * 닉네임으로 전체 사용자를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 목록
     */
    @Operation(summary = "Get / Search User by Nickname", description = "닉네임으로 전체 사용자를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<UserListDTO> searchUserByNickname(@RequestParam String nickname) {
        UserListDTO user = friendService.findUserByNickname(nickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with nickname: " + nickname));
        return ResponseEntity.ok(user);
    }


    /**
     * 친구 추가 요청을 보냅니다.
     *
     * @param friendRequestDTO 친구 요청 정보
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post / Friend Request", description = "친구 추가 요청을 보냅니다.")
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO) {
        friendService.sendFriendRequest(friendRequestDTO);
        return ResponseEntity.ok("Friend request sent successfully.");
    }


    /**
     * 특정 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 요청 목록
     */
    @Operation(summary = "Get / Friend Requests", description = "특정 사용자가 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/requests/{userId}")
    public ResponseEntity<List<FriendRequestDTO>> getFriendRequests(@PathVariable Long userId) {
        List<FriendRequestDTO> friendRequests = friendService.getFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    /**
     * 친구 요청에 응답합니다.
     *
     * @param senderNickname 친구 요청을 보낸 사용자의 닉네임
     * @param receiverNickname 친구 요청을 받은 사용자의 닉네임
     * @param accepted true이면 요청 수락, false이면 요청 거절
     * @return 응답 메시지
     */
    @Operation(summary = "Post / Friend Request Response", description = "친구 요청에 응답합니다.")
    @PostMapping("/respond")
    public ResponseEntity<String> respondToFriendRequest(@RequestParam String senderNickname, @RequestParam String receiverNickname, @RequestParam boolean accepted) {
        friendService.respondToFriendRequest(senderNickname, receiverNickname, accepted);
        return ResponseEntity.ok("Friend request " + (accepted ? "accepted" : "rejected") + " successfully.");
    }


    /**
     * 친구 관계를 삭제합니다.
     * TODO: 나중에 jwt토큰에서 사용자 ID를 추출하는 로직으로 변경
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete / Delete Friend", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendService.deleteFriendById(userId, friendId);
        return ResponseEntity.noContent().build();
    }



}
