package com.elice.ustory.domain.friend.controller;

import com.elice.ustory.domain.friend.dto.FriendRequestDto;
import com.elice.ustory.domain.friend.dto.FriendRequestListDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.FriendResponseDto;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Tag(name = "Friend API")
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
    public ResponseEntity<List<UserFriendDTO>> getFriends(
            @JwtAuthorization Long userId,
            @RequestParam(required = false) String nickname,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "requestTime") LocalDateTime requestTime) {

        if (page < 1) {
            throw new ValidationException("페이지는 1 이상이어야 합니다.");
        } else if (size < 1){
            throw new ValidationException("사이즈는 1 이상이어야합니다.");
        }
        Pageable pageable = PageRequest.of(page - 1, size);

        List<UserFriendDTO> friends = friendService.getFriends(userId, nickname, requestTime, pageable);

        return ResponseEntity.ok(friends);
    }

    /**
     * 친구 추가 요청을 보냅니다.
     *
     * @param userId 요청을 보낸 사용자의 ID
     * @param friendRequestDto 친구 요청을 받을 사용자의 닉네임
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post / Friend Request", description = "친구 추가 요청을 보냅니다.")
    @PostMapping
    public ResponseEntity<Void> sendFriendRequest(@JwtAuthorization Long userId, @Valid @RequestBody FriendRequestDto friendRequestDto) {
        validateNickname(friendRequestDto.getReceiverNickname());
        friendService.sendFriendRequest(userId, friendRequestDto);
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
    public ResponseEntity<List<FriendRequestListDTO>> getFriendRequests(@JwtAuthorization Long userId,
                                                                        @RequestParam(name = "page", defaultValue = "1") int page,
                                                                        @RequestParam(name = "size", defaultValue = "10") int size,
                                                                        @RequestParam(name = "requestTime") LocalDateTime requestTime) {
        if (page < 1) {
            throw new ValidationException("페이지는 1 이상이어야 합니다.");
        } else if (size < 1){
            throw new ValidationException("사이즈는 1 이상이어야합니다.");
        }
        Pageable pageable = PageRequest.of(page - 1, size);

        List<FriendRequestListDTO> friendRequests = friendService.getFriendRequests(userId, requestTime, pageable);
        return ResponseEntity.ok(friendRequests);
    }

    /**
     * 친구 요청에 응답합니다.
     *
     * @param userId 친구 요청을 받은 사용자의 ID
     * friendRequestDto 친구 요청을 받을 사용자의 닉네임을 포함한 DTO
     * @return 응답 메시지
     */
    @Operation(summary = "Post / Friend approve", description = "친구 요청에 응답합니다.")
    @PostMapping("/approve")
    public ResponseEntity<String> respondToFriendRequest(@JwtAuthorization Long userId, @Valid @RequestBody FriendResponseDto friendResponseDto) {
        validateNickname(friendResponseDto.getSenderNickname());
        friendService.respondToFriendRequest(userId, friendResponseDto);
        return ResponseEntity.ok("친구요청 " + (friendResponseDto.isAccepted() ? "수락" : "거절") + "이 되었습니다.");
    }


    /**
     * 친구 관계를 삭제합니다.
     * @param userId 현재 사용자의 ID
     * @param friendNickname 삭제할 친구의 닉네임
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete / Delete Friend", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/{friendNickname}")
    public ResponseEntity<Void> deleteFriend(@JwtAuthorization Long userId, @PathVariable String friendNickname) {
        friendService.deleteFriendById(userId, friendNickname);
        return ResponseEntity.noContent().build();
    }


    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new ValidationException("닉네임값이 잘못 들어왔습니다.");
        }
    }


}
