package com.elice.ustory.domain.page.service;

import com.elice.ustory.domain.page.entity.Address;
import com.elice.ustory.domain.page.entity.Image;
import com.elice.ustory.domain.page.entity.Page;
import com.elice.ustory.domain.page.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;

    @Transactional
    public Page createPage(Page page, List<Image> images, Address address) {

        Page savedPage = pageRepository.save(page);

        for (Image image : images) {
            savedPage.addImage(image);
        }

        savedPage.setAddress(address);

        return savedPage;
    }

    // TODO : 팀원들끼리 예외처리 정리해서 진행할 것
    public Page getPageById(long Id) {
        return pageRepository.findById(Id).orElse(null);
    }

    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    @Transactional
    public Page updatePage(Page page) {

        Page previousPage = pageRepository.findById(page.getId()).orElse(null);

        previousPage.update(page.getTitle(), page.getThumbnailImage(), page.getVisitedAt());
        return previousPage;
    }

    public void deleteById(Long Id) {
        Page getDeletePage = pageRepository.findById(Id).orElse(null);
        getDeletePage.delete();
    }

}