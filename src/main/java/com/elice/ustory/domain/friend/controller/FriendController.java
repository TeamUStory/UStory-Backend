package com.elice.ustory.domain.friend.controller;

import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.global.jwt.JwtAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "friend", description = "Friend API")
@RestController
@RequestMapping("/friend")
public class FriendController {

    private FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * 사용자의 전체 친구 리스트를 조회하거나 닉네임으로 친구를 검색합니다.
     *
     * @param userId   조회할 사용자의 ID (옵션)
     * @param nickname 검색할 닉네임 (옵션)
     * @return 친구 목록 또는 검색된 친구 목록
     */
    @Operation(summary = "Get / Friends", description = "사용자의 전체 친구 리스트를 조회하거나 닉네임으로 친구를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<UserFriendDTO>> getFriends(@JwtAuthorization Long userId, @RequestParam(required = false) String nickname) {
        List<UserFriendDTO> friends = friendService.getFriends(userId, nickname);
        return ResponseEntity.ok(friends);
    }

    /**
     * 친구 추가 요청을 보냅니다.
     *
     * @param friendRequestDTO 친구 요청 정보
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post / Friend Request", description = "친구 추가 요청을 보냅니다.")
    @PostMapping
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO) {
        friendService.sendFriendRequest(friendRequestDTO);
        return ResponseEntity.ok("친구 요청이 성공적으로 전송되었습니다.");
    }

    /**
     * 특정 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 요청 목록
     */
    @Operation(summary = "Get / Friend Requests", description = "특정 사용자가 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestDTO>> getFriendRequests(@JwtAuthorization Long userId) {
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
    @PostMapping("/approve")
    public ResponseEntity<String> respondToFriendRequest(@RequestParam String senderNickname, @RequestParam String receiverNickname, @RequestParam boolean accepted) {
        friendService.respondToFriendRequest(senderNickname, receiverNickname, accepted);
        return ResponseEntity.ok("친구요청 " + (accepted ? "수락" : "거절") + "이 되었습니다.");
    }


    /**
     * 친구 관계를 삭제합니다.
     * @param userId 현재 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete / Delete Friend", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(Long userId, @PathVariable Long friendId) {
        friendService.deleteFriendById(userId, friendId);
        return ResponseEntity.noContent().build();
    }



}
