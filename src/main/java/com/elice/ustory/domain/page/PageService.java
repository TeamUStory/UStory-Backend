package com.elice.ustory.domain.page;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {

    @Autowired
    private final PageRepository pageRepository;

    // TODO : 팀원들끼리 예외처리 정리해서 진행할 것
    public Page getPageById(long Id) {
        return pageRepository.findById(Id).orElse(null);
    }

    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    public Page postPage(Page page) {
        return pageRepository.save(page);
    }

    // TODO : 빌더 관련 질문
    public Page updatePage(Page page) {
        Page previousPage = pageRepository.findById(page.getId()).orElse(null);
        Page updatedPage = previousPage.toBuilder()
                .mainImageURI(page.getMainImageURI())
                .visitedAt(page.getVisitedAt())
//                .writerId(previousPage.getWriterId())
//                .diaryId(previousPage.getDiaryId())
//                .addressId(previousPage.getAddressId())
//                .comments(previousPage.getComments())
                .deletedAt(previousPage.getDeletedAt())
                .locked(previousPage.getLocked())
//                .createdAt(previousPage.getCreatedAt())
//                .modifiedAt(LocalDateTime.now())
                .build();
        pageRepository.save(updatedPage);
        return updatedPage;
    }

    // TODO : 이것도 빌더로?
    public void deleteById(Long Id) {
        Page getDeletePage = pageRepository.findById(Id).orElse(null);
        getDeletePage.setDeletedAt(LocalDateTime.now());
    }

}