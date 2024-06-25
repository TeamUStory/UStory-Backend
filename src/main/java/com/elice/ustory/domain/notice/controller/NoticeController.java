package com.elice.ustory.domain.notice.controller;

import com.elice.ustory.domain.notice.dto.NoticeResponse;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Tag(name = "Notice API")
@RestController
@RequestMapping("/notices")
public class NoticeController {


    private NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }


    /**
     * 특정 사용자의 모든 알림을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 알림 목록
     */
    @Operation(summary = "Get All Notice API",
            description = "사용자가 가진 모든 알림 조회, type: 친구, 기록, 코멘트 중 하나로 반납합니다. 페이퍼로 이동해야 할 상황을 고려해서 paperId를 함께 넘기며, 만약 null 값이라면 친구와 관련된 API라고 생각하시면 됩니다. <br> 출력할 데이터가 존재하지 않는 경우 빈 리스트를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NoticeResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNoticesByUserId(@JwtAuthorization Long userId,
                                                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                                                      @RequestParam(name = "requestTime") LocalDateTime requestTime) {
        if (page < 1) {
            throw new ValidationException("페이지는 1 이상이어야 합니다.");
        } else if (size < 1){
            throw new ValidationException("사이즈는 1 이상이어야 합니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        List<NoticeResponse> notices = noticeService.getAllNoticesByUserId(userId, requestTime, pageable);

        return ResponseEntity.ok(notices);
    }


    /**
     * 알림을 삭제합니다.
     *
     * @param noticeId 삭제할 알림의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete / Notice", description = "알림을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@JwtAuthorization Long userId, @PathVariable Long noticeId) {
        noticeService.deleteNoticeById(userId, noticeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자의 모든 알림을 삭제합니다.
     * @param userId
     * @return* *요청 성공 여부*
     **/
    @Operation(summary = "Delete All Notices", description = "사용자의 모든 알림을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllNotices(@JwtAuthorization Long userId) {
        noticeService.deleteAllNoticesByUserId(userId);
        return ResponseEntity.noContent().build();
    }


}
