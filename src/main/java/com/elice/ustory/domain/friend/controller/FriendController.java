package com.elice.ustory.domain.friend.controller;

import com.elice.ustory.domain.friend.dto.FriendRequestDto;
import com.elice.ustory.domain.friend.dto.FriendRequestListDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.FriendResponseDto;
import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.global.Validation.PageableValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Tag(name = "Friend API")
@RestController
@RequestMapping("/friend")
public class FriendController {

    private PageableValidation pageableValidation;
    private FriendService friendService;

    public FriendController(FriendService friendService, PageableValidation codeutils) {
        this.pageableValidation = codeutils;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserFriendDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserFriendDTO>> getFriends(
            @JwtAuthorization Long userId,
            @RequestParam(required = false) String nickname,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "requestTime") LocalDateTime requestTime) {

        Pageable pageable = pageableValidation.madePageable(page, size);

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
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "Get / Friend received", description = "특정 사용자가 받은 친구 요청 목록을 조회합니다. <br> 친구 요청 받은 기록이 없다면 빈 리스트를 출력합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendRequestListDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestListDTO>> getFriendRequests(@JwtAuthorization Long userId,
                                                                        @RequestParam(name = "page", defaultValue = "1") int page,
                                                                        @RequestParam(name = "size", defaultValue = "10") int size,
                                                                        @RequestParam(name = "requestTime") LocalDateTime requestTime) {

        Pageable pageable = pageableValidation.madePageable(page, size);

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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
