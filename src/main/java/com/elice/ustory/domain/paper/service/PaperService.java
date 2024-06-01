package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepositoryCustom;
import com.elice.ustory.domain.notice.dto.PaperNoticeRequest;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.GlobalExceptionHandler;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaperService {

    private final PaperRepository paperRepository;
    private final NoticeService noticeService;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public Paper createPaper(Paper paper, List<Image> images, Address address, Users writer, Diary diary) {

        Paper savedPaper = paperRepository.save(paper);

        savedPaper.updateWriter(writer);

        savedPaper.updateDiary(diary);

        savedPaper.updateImages(images);

        savedPaper.setAddress(address);

        needCommentNotice(diary, paper);

        return savedPaper;
    }

    public Paper getPaperById(long Id) {
        return checkPaperAndDeleted(Id);
    }

    /**
     * 다이어리 내에 존재하는 Papers 최신순으로 페이지네이션
     */
    public List<Paper> getPapersByDiaryId(Long diaryId, int page, int size) {
        // TODO : 여기 deleted 체크 하고 싶은데, 그러면 리스트 for each 문이나 이터레이터로 돌면서 체크해야 하나요?
        return paperRepository.findByDiaryId(diaryId, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
    }

    /**
     * 작성한 Papers 최신순으로 페이지네이션
     */
    public List<Paper> getPapersByWriterId(Long writerId, int page, int size) {
        return paperRepository.findByWriterId(writerId, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
    }

    /**
     * 사용자가 속해있는 다이어리의 모든 Paper 불러오기
     */
    public List<Paper> getPapersByUserId(Long userId) {
        return paperRepository.findAllPapersByUserId(userId);
    }

    @Transactional
    public Paper updatePaper(Long savedPaperId, Paper paper, List<Image> images, Address address) {

        Paper previousPaper = checkPaperAndDeleted(savedPaperId);

        previousPaper.update(
                paper.getTitle(),
                paper.getThumbnailImageUrl(),
                paper.getVisitedAt()
        );

        previousPaper.updateImages(images);

        previousPaper.setAddress(address);

        return previousPaper;
    }

    public void deleteById(Long Id) {
        Paper paper = paperRepository.findById(Id).orElseThrow(() -> new NotFoundException("해당 페이퍼를 찾을 수 없습니다", ErrorCode.NOT_FOUND_EXCEPTION));
        paper.softDelete();
    }

    public Paper checkPaperAndDeleted(Long paperId) {
        Paper checkedUser = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException("해당 페이퍼를 찾을 수 없습니다", ErrorCode.NOT_FOUND_EXCEPTION));
        if (checkedUser.getDeletedAt() != null) {
            throw new UnauthorizedException("해당 페이퍼에 접근 권한이 없습니다", ErrorCode.VALIDATION_EXCEPTION);
        }
        return checkedUser;
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
                        .receiverId(userRepository.findByNickname(nickName).orElseThrow(()->new NotFoundException("해당 유저를 찾을 수 없습니다.")).getId())
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
        Paper checkedPaper = checkPaperAndDeleted(paper.getId());

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