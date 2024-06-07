package com.elice.ustory.domain.notice.service;

import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.domain.notice.dto.NoticeResponse;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.repository.NoticeRepository;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.util.NoticeUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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
        return noticeRepository.findByResponseId(userId).stream()
                .map(this::convertToNoticeResponse)
                .collect(Collectors.toList());
    }


    private NoticeResponse convertToNoticeResponse(Notice notice) {
        NoticeResponse noticeResponse = new NoticeResponse();
        noticeResponse.setMessage(notice.getMessage());
        noticeResponse.setTime(notice.getCreatedAt());

        switch (notice.getMessageType()) {
            case 1, 3 -> noticeResponse.setType("친구");
            case 2 -> {
                noticeResponse.setType("코멘트");
                noticeResponse.setPaperId(notice.getResponseId());
                noticeResponse.setTime(paperRepository.findById(noticeResponse.getPaperId())
                        .orElseThrow(() -> new NotFoundException("페이퍼를 찾을 수 없습니다."))
                        .getCreatedAt());
            }
            case 4 -> {
                noticeResponse.setType("기록");
                noticeResponse.setPaperId(notice.getResponseId());
            }
            default ->  throw new ValidationException("잘못된 메시지 타입입니다.");
        }
        return noticeResponse;
    }


    /**
     * 공통 알림 전송 메서드
     *
     * @param noticeRequest 알림 DTO
     */
    @Transactional
    public void sendNotice(@Valid NoticeRequest noticeRequest) {

        String message;
        Long requestId;

        switch (noticeRequest.getMessageType()) {
            case 3 -> {
                Users findUser = userRepository.findById(noticeRequest.getSenderId())
                        .orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."));
                message = NoticeUtils.generateMessage(noticeRequest, findUser.getNickname());
                requestId = noticeRequest.getSenderId();
            }
            case 1 -> {
                message = NoticeUtils.generateMessage(noticeRequest);
                requestId = noticeRequest.getSenderId();
            }
            case 2, 4 -> {
                if (noticeRequest.getPaperId() == null) {
                    throw new ValidationException("페이퍼 ID가 null입니다.");
                }
                message = NoticeUtils.generateMessage(noticeRequest);
                requestId = noticeRequest.getPaperId();
            }
            default -> throw new ValidationException("잘못된 메시지 타입입니다.");
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
     * @param userId 로그인한 사용자의 아이디
     * @param id 삭제할 알림의 ID
     */
    public void deleteNoticeById(Long userId, Long id) {
        if (id == null) {
            throw new ValidationException("알림 ID가 null입니다.");
        }
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다."));
        if (notice.getResponseId() != userId) {
            throw new UnauthorizedException("해당 알림을 삭제할 권한이 없습니다.");
        }
        noticeRepository.delete(notice);
    }

    /**
     * 특정 조건으로 알림을 삭제합니다.
     *
     * @param requestId 알림을 보낸 사용자의 ID
     * @param responseId 알림을 받은 사용자의 ID
     * @param messageType 알림의 유형
     */
    public void deleteNoticeBySender(Long requestId, Long responseId, int messageType) {
        if (requestId == null || responseId == null) {
            throw new ValidationException("requestId 또는 responseId가 null입니다.");
        }
        noticeRepository.findByRequestIdAndResponseIdAndMessageType(requestId, responseId, messageType)
                .ifPresent(noticeRepository::delete);
    }
}
