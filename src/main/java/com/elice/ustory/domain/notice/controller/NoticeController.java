package com.elice.ustory.domain.notice.controller;

import com.elice.ustory.domain.notice.dto.NoticeResponse;
import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "notice", description = "Notice API")
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
            description = "사용자가 가진 모든 알림 조회, type: 친구, 기록, 코멘트 중 하나로 반납합니다. 페이퍼로 이동해야 할 상황을 고려해서 paperId를 함께 넘기며, 만약 null 값이라면 친구와 관련된 API라고 생각하시면 됩니다.")
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNoticesByUserId(@JwtAuthorization Long userId) {
        List<NoticeResponse> notices = noticeService.getAllNoticesByUserId(userId);
        return ResponseEntity.ok(notices);
    }


    /**
     * 알림을 삭제합니다.
     *
     * @param noticeId 삭제할 알림의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete / Notice", description = "알림을 삭제합니다.")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNoticeById(noticeId);
        return ResponseEntity.noContent().build();
    }


}
