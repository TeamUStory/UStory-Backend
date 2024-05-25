package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperService {

    private final PaperRepository paperRepository;

    @Transactional
    public Paper createPaper(Paper paper, List<Image> images, Address address) {

        Paper savedPaper = paperRepository.save(paper);

        savedPaper.updateImages(images);

        savedPaper.setAddress(address);

        return savedPaper;
    }

    // TODO : 팀원들끼리 예외처리 정리해서 진행할 것
    public Paper getPaperById(long Id) {
        return paperRepository.findById(Id).orElse(null);
    }

    // TODO: 삭제 예정인 코드
    public List<Paper> getAllPapers() {
        return paperRepository.findAll();
    }

    @Transactional
    public Paper updatePaper(Long savedPaperId, Paper paper, List<Image> images, Address address) {

        Paper previousPaper = paperRepository.findById(savedPaperId).orElse(null);

        previousPaper.update(
                paper.getTitle(),
                paper.getThumbnailImage(),
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