package com.elice.ustory.domain.notice.controller;

import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "notice", description = "Notice API")
@RestController
@RequestMapping("api/notice")
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
//    @Operation(summary = "Get Notice API", description = "사용자의 모든 알림 조회")
//    @GetMapping("/notices/{userId}")
//    public ResponseEntity<List<Notice>> getAllNoticesByUserId(@PathVariable Long userId) {
//        List<Notice> notices = noticeService.getAllNoticesByUserId(userId);
//        return ResponseEntity.ok(notices);
//    }


    /**
     * 친구 요청 알람에 응답합니다.
     *
     * @param noticeId 알람 ID
     * @param accepted true이면 수락, false이면 거절
     * @return 요청 성공 여부
     */
    @Operation(summary = "Post Notice API", description = "친구 요청 알림 응답")
    @PostMapping("/{noticeId}/response")
    public ResponseEntity<Void> respondToFriendRequest(@PathVariable Long noticeId, @RequestParam boolean accepted) {
        noticeService.respondToFriendRequest(noticeId, accepted);
        return ResponseEntity.noContent().build();
    }




}
