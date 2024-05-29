package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
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
    public Paper createPaper(Paper paper, List<Image> images, Address address/*, Users writer, Diary diary*/) {

        Paper savedPaper = paperRepository.save(paper);

//        savedPaper.updateWriter(writer);
//
//        savedPaper.updateDiary(diary);

        savedPaper.updateImages(images);

        savedPaper.setAddress(address);

        return savedPaper;
    }

    public Paper getPaperById(long Id) {
        return paperRepository.findById(Id).orElse(null);
    }

    /** 다이어리 내에 존재하는 Papers 최신순으로 페이지네이션 */
    public List<Paper> getPapersByDiaryId(Long diaryId, int page, int size) {
        return paperRepository.findByDiaryId(diaryId, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "modifiedAt")));
    }

    /** 작성한 Papers 최신순으로 페이지네이션 */
    public List<Paper> getPapersByWriterId(Long writerId, int page, int size) {
        return paperRepository.findByWriterId(writerId, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "modifiedAt")));
    }

    /** 북마크로 체크한 Papers 페이지네이션 */
    // TODO: 작성해야함...

    @Transactional
    public Paper updatePaper(Long savedPaperId, Paper paper, List<Image> images, Address address) {

        Paper previousPaper = paperRepository.findById(savedPaperId).orElse(null);

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
        Paper paper = paperRepository.findById(Id).orElse(null);
        paper.softDelete();
    }

}