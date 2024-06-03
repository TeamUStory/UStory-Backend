package com.elice.ustory.domain.notice.service;

import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.domain.notice.dto.NoticeResponse;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.util.CommonUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;

    /**
     * 특정 사용자의 모든 알림을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 알림 목록
     */
    public List<NoticeResponse> getAllNoticesByUserId(Long userId) {
        List<Notice> getByResponseId = noticeRepository.findByResponseId(userId);
        List<NoticeResponse> noticeResponses = new ArrayList<>();
        for (Notice notice : getByResponseId) {
            NoticeResponse noticeResponse = new NoticeResponse();
            noticeResponse.setMessage(notice.getMessage());

            switch (notice.getMessageType()) {
                case 1 -> {
                    // 여기에는 친구 노티스의 요청 시간이 시간으로만 셋이 되면 된다. 리퀘스트 ID
                    noticeResponse.setType("친구");
                    noticeResponse.setTime(LocalDateTime.of(1111, 1, 1, 1, 11, 11));
                }
                case 2 -> {
                    noticeResponse.setType("코멘트");
                    noticeResponse.setPaperId(notice.getRequestId());
                    noticeResponse.setTime(paperRepository.findById(noticeResponse.getPaperId())
                            .orElseThrow(() -> new NotFoundException("유저 못찾음"))
                            .getCreatedAt());
                }
                case 3 -> {
                    // 여기에는 친구 노티스의 수락 시간이 셋이 되야 한다. 리스판스 ID
                    noticeResponse.setType("친구");
                    noticeResponse.setTime(LocalDateTime.of(3333, 3, 3, 3, 33, 33));
                }
                case 4 -> {
                    noticeResponse.setType("기록");
                    noticeResponse.setPaperId(notice.getRequestId());
                    noticeResponse.setTime(paperRepository.findById(noticeResponse.getPaperId())
                            .orElseThrow(() -> new NotFoundException("유저 못찾음"))
                            .getUnLockedAt());
                }
            }
            noticeResponses.add(noticeResponse);
        }
        return noticeResponses;
    }

    /**
     * 공통 알림 전송 메서드
     *
     * @param noticeRequest 알림 DTO
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

        // 알림 저장
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
     * @param requestId 알림을 보낸 사용자의 ID
     * @param responseId 알림을 받은 사용자의 ID
     * @param messageType 알림의 유형
     */
    public void deleteNoticeBySender(Long requestId, Long responseId, int messageType) {
        Optional<Notice> noticeOptional = noticeRepository.findByRequestIdAndResponseIdAndMessageType(requestId, responseId, messageType);
        noticeOptional.ifPresent(noticeRepository::delete);
    }
}
