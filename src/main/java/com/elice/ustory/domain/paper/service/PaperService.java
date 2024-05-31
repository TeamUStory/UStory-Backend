package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.GlobalExceptionHandler;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperService {

    private final PaperRepository paperRepository;

    @Transactional
    public Paper createPaper(Paper paper, List<Image> images, Address address, Users writer, Diary diary) {

        Paper savedPaper = paperRepository.save(paper);

        savedPaper.updateWriter(writer);

        savedPaper.updateDiary(diary);

        savedPaper.updateImages(images);

        savedPaper.setAddress(address);

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
        checkPaperAndDeleted(writerId);
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

}