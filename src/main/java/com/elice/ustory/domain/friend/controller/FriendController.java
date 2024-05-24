package com.elice.ustory.domain.friend.controller;

import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.user.entity.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Tag(name = "friend", description = "Friend API")
@RestController
@RequestMapping("api/friend")
public class FriendController {

    private FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * 사용자의 전체 친구 리스트를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 친구 목록
     */
    @Operation(summary = "Get Friend API", description = "친구리스트 userId를 통해 불러옴.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Users>> getAllFriends(@PathVariable Long userId) {
        List<Users> friends = friendService.getAllFriends(userId);
        return ResponseEntity.ok(friends);
    }

    /**
     * 닉네임을 이용하여 사용자를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 (없으면 404 Not Found)
     */
    @Operation(summary = "Get Friend API", description = "닉네임으로 검색함.")
    @GetMapping("/search")
    public ResponseEntity<Users> searchUserByNickname(@RequestParam String nickname) {
        Users user = friendService.searchUserByNickname(nickname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(user);
    }

    /**
     * 닉네임을 이용하여 친구 요청을 보냅니다.
     * TDOD: 추후에 토큰 보내주는거 작성해야됨
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 (없으면 404 Not Found)
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post Friend API", description = "친구요청 보냄")
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestParam Long senderId, @RequestParam String nickname) {
        friendService.sendFriendRequest(senderId, nickname);
        return ResponseEntity.ok("Friend request sent successfully.");
    }


    /**
     * 친구 요청을 수락하고 친구 관계를 추가합니다.
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post Friend API", description = "친구 수락 후 친구 추가 됨")
    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        friendService.acceptFriendRequest(senderId, receiverId);
        return ResponseEntity.ok("Friend request accepted successfully.");
    }

    /**
     * 친구 요청을 거절합니다.
     *
     * @param senderId 친구 요청을 보낸 사용자의 ID
     * @param receiverId 친구 요청을 받은 사용자의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post Friend API", description = "친구요청 거절")
    @PostMapping("/reject")
    public ResponseEntity<String> rejectFriendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        friendService.rejectFriendRequest(senderId, receiverId);
        return ResponseEntity.ok("Friend request rejected successfully.");
    }

    /**
     * 친구 관계를 삭제합니다.
     * TDOD: jwt토큰 id 보내줘야됨.
     * @param userId 현재 사용자의 ID > friendId
     * @param friendId 삭제할 친구의 ID > request HTTP 요청 객체 (토큰을 추출하기 위해 사용)
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete Friend API", description = "친구삭제")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void>deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendService.deleteFriendById(userId, friendId);
        return ResponseEntity.noContent().build();
    }



}
