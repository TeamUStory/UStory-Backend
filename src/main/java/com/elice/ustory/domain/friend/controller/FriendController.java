package com.elice.ustory.domain.friend.controller;

import com.elice.ustory.domain.friend.dto.FriendRequestListDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.elice.ustory.global.exception.ErrorCode.VALIDATION_PARAMETER_EXCEPTION;


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
     * @param userId 요청을 보낸 사용자의 ID
     * @param receiverNickname 친구 요청을 받을 사용자의 닉네임
     * @return 요청 성공 여부
     */
    @Operation(summary = "Get / Friend Request", description = "친구 추가 요청을 보냅니다.")
    @GetMapping
    public ResponseEntity<Void> sendFriendRequest(@JwtAuthorization Long userId, @RequestParam String receiverNickname) {
        if (receiverNickname == null || receiverNickname.trim().isEmpty()) {
            throw new ValidationException("닉네임값이 잘못 들어왔습니다.");
        }
        friendService.sendFriendRequest(userId, receiverNickname);
        return ResponseEntity.noContent().build();
    }


    /**
     * 특정 사용자가 받은 친구 요청 목록을 조회합니다.
     *
     * @param userId 사용자의 ID
     * @return 친구 요청 목록
     */
    @Operation(summary = "Get / Friend received", description = "특정 사용자가 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestListDTO>> getFriendRequests(@JwtAuthorization Long userId) {
        List<FriendRequestListDTO> friendRequests = friendService.getFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    /**
     * 친구 요청에 응답합니다.
     *
     * @param userId 친구 요청을 받은 사용자의 ID
     * @param senderNickname 친구 요청을 보낸 사용자의 닉네임
     * @param accepted true이면 요청 수락, false이면 요청 거절
     * @return 응답 메시지
     */
    @Operation(summary = "Post / Friend approve", description = "친구 요청에 응답합니다.")
    @PostMapping("/approve")
    public ResponseEntity<String> respondToFriendRequest(@JwtAuthorization Long userId, @RequestParam String senderNickname, @RequestParam boolean accepted) {
        friendService.respondToFriendRequest(userId, senderNickname, accepted);
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
    public ResponseEntity<Void> deleteFriend(@JwtAuthorization Long userId, @PathVariable Long friendId) {
        friendService.deleteFriendById(userId, friendId);
        return ResponseEntity.noContent().build();
    }



}
