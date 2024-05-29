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
    @Operation(summary = "Get Notice API", description = "사용자의 모든 알림 조회")
    @GetMapping("/notices/{userId}")
    public ResponseEntity<List<Notice>> getAllNoticesByUserId(@PathVariable Long userId) {
        List<Notice> notices = noticeService.getAllNoticesByUserId(userId);
        return ResponseEntity.ok(notices);
    }





    /**
     * 알림을 삭제합니다.
     *
     * @param id 삭제할 알림의 ID
     * @return 요청 성공 여부
     */
    @Operation(summary = "Delete / Notice", description = "알림을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNoticeById(id);
        return ResponseEntity.noContent().build();
    }




}
