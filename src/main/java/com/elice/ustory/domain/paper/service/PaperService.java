package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.notice.dto.PaperNoticeRequest;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaperService {

    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";
    private static final String NOT_FOUND_DIARY_MESSAGE = "%d: 해당하는 다이어리가 존재하지 않습니다.";
    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";
    private static final String ORDER_BY_UPDATED_AT = "updatedAt";

    private final PaperRepository paperRepository;
    private final NoticeService noticeService;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public Paper createPaper(Paper paper, Long writerId, Long diaryId) {

        Users writer = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER_MESSAGE, writerId)));
        paper.addWriter(writer);

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId)));
        paper.addDiary(diary);

        Paper savedPaper = paperRepository.save(paper);

        needCommentNotice(diary, paper);

        return savedPaper;
    }

    public Paper getPaperById(long Id) {
        return validatePaper(Id);
    }

    /**
     * 다이어리 내에 존재하는 Papers 최신순으로 페이지네이션
     */
    public Slice<Paper> getPapersByDiaryId(Long diaryId, int page, int size, LocalDate startDate, LocalDate endDate) {

        // 다이어리 검증
        diaryRepository.findById(diaryId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId)));

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return paperRepository.findAllByDiaryIdAndDateRange(diaryId, startDate, endDate, pageRequest);
    }

    /**
     * 작성한 Papers 최신순으로 페이지네이션
     */
    public List<Paper> getPapersByWriterId(Long writerId, int page, int size) {
        return paperRepository.findByWriterId(writerId, PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, ORDER_BY_UPDATED_AT)));
    }

    /**
     * 사용자가 속해있는 다이어리의 모든 Paper 불러오기
     */
    public List<Paper> getPapersByUserId(Long userId) {
        return paperRepository.findAllPapersByUserId(userId);
    }

    @Transactional
    public Paper updatePaper(Long paperId, Paper paper) {

        Paper previousPaper = validatePaper(paperId);

        previousPaper.update(
                paper.getTitle(),
                paper.getThumbnailImageUrl(),
                paper.getVisitedAt()
        );

        return previousPaper;
    }

    public void deleteById(Long paperId) {
        paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)))
                .softDelete();
    }

    public Paper validatePaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        if (paper.isDeleted()) {
            throw new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId));
        }

        return paper;
    }

    public Integer countPapersByWriterId(Long userId) {
        List<Paper> findByWriterId = paperRepository.findByWriterId(userId);
        return findByWriterId.size();
    }

    // 작성자를 제외한 멤버들에게 코멘트를 달아달라고 알림 전송
    public void needCommentNotice(Diary diary, Paper paper) {
//         TODO : 다이어리에 속한 멤버들 전체 다 가져오기
        List<String> userFindByDiary = diaryUserRepository.findUserByDiary(diary.getId());
//
//         속한 멤버들 중 작성자 제거하기
        userFindByDiary.remove(paper.getWriter().getNickname());
//
//        // 작성자를 제외한 남은 멤버가 들어가있는 다이어리-유저 리스트에다가 알림 보내기
        if (!userFindByDiary.isEmpty()) {
            for (String nickName : userFindByDiary) {
                PaperNoticeRequest paperNoticeRequest = PaperNoticeRequest.builder()
                        .receiverId(userRepository.findByNickname(nickName).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다.")).getId())
                        .paper(paper)
                        .messageType(2)
                        .build();
                noticeService.sendNotice(paperNoticeRequest);
            }
        }
    }

    // 준용아 DB에 저장 되고 호출해줘야한다?
    @Transactional
    public void noticeLocked(Diary diary, Paper paper) {
        // 페이퍼가 존재하는지, 삭제되었는지에 대해서 체크 (예외처리 동시 진행), Q : 검증 완료된 상황 아닌가?
        Paper checkedPaper = validatePaper(paper.getId());

        // 체크된 페이퍼에서 모든 코멘트를 불러온다.
        List<Comment> comments = paper.getComments();

        // Id 중복 체크를 위한 함수를 만든 뒤, 스트림으로 채워준다.
        Set<Long> userIds = comments.stream()
                .map(comment -> comment.getUser().getId())
                .collect(Collectors.toSet());

        // TODO : 다이어리에 속한 유저 List를 가져온다. 현재 다이어리에서 유저를 불러올 수 없기 때문에 주석 처리.
        List<String> userFindByDiary = diaryUserRepository.findUserByDiary(diary.getId());

        // TODO : 다이어리에 속한 유저 List와 set 사이즈 비교 후, 일치하면 바꾸고 노티스 던진다. 현재 다이어리에서 유저를 불러올 수 없기 때문에 주석 처리.
        if (userIds.size() == userFindByDiary.size()) {
            paper.unLock();
            if (userIds.iterator().hasNext()) {
                for (Long userId : userIds) {
                    PaperNoticeRequest paperNoticeRequest = PaperNoticeRequest.builder()
                            .receiverId(userId)
                            .paper(paper)
                            .messageType(4)
                            .build();
                    noticeService.sendNotice(paperNoticeRequest);
                }
            }
        }
    }

}