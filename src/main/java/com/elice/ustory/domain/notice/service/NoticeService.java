package com.elice.ustory.domain.notice.service;

import com.elice.ustory.domain.friend.service.FriendService;
import com.elice.ustory.domain.notice.dto.NoticeDTO;
import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.util.CommonUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private FriendService friendService;



    @Autowired
    public NoticeService(NoticeRepository noticeRepository, UserRepository userRepository, PaperRepository paperRepository, @Lazy FriendService friendService) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
        this.friendService = friendService;
    }

    /**
     * 특정 사용자의 모든 알림을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 알림 목록
     */
    public List<Notice> getAllNoticesByUserId(Long userId) {
        return noticeRepository.findByResponseId(userId);
    }



    /**
     * 공통 알림 전송 메서드
     *
//     * @param noticeDTO 알림 DTO
     */
    @Transactional
    public void sendNotice(NoticeRequest noticeRequest) {
        String message;
        Long requestId;

        if (noticeRequest.getMessageType() == 3) {
            Users findUser = userRepository.findById(noticeRequest.getSenderId()).orElseThrow(() -> new NotFoundException("해당 유저 없음"));
            message = CommonUtils.generateMessage(noticeRequest, findUser.getNickname());
            Long senderId = noticeRequest.getSenderId();
            requestId = senderId;
        } else if (noticeRequest.getMessageType() == 1) {
            message = CommonUtils.generateMessage(noticeRequest);
            Long senderId = noticeRequest.getSenderId();
            requestId =senderId;
        } else {
            message = CommonUtils.generateMessage(noticeRequest);
            Long paperId = noticeRequest.getPaperId();
            requestId = paperId;
        }

        Notice notice = Notice.builder()
                .requestId(requestId)
                .responseId(noticeRequest.getResponseId())
                .message(message)
                .messageType(noticeRequest.getMessageType())
                .build();

//        // 로그 추가
        System.out.println("Notice before save: " + notice);
//
//        // 알림 저장
        noticeRepository.save(notice);
    }



    /**
     * 알림을 ID로 삭제합니다.
     *
     * @param id 삭제할 알림의 ID
     */
    public void deleteNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
        noticeRepository.delete(notice);
    }

    /**
     * 특정 조건으로 알림을 삭제합니다 (senderId 기준).
     *
//     * @param senderId 알림을 보낸 사용자의 ID
//     * @param receiverId 알림을 받은 사용자의 ID
     * @param messageType 알림의 유형
     */
    public void deleteNoticeBySender(Long requestId, Long responseId, int messageType) {
        Optional<Notice> noticeOptional = noticeRepository.findByRequestIdAndResponseIdAndMessageType(requestId, responseId, messageType);
        noticeOptional.ifPresent(noticeRepository::delete);
    }
}
