package com.elice.ustory.domain.recommand;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.address.AddressRepository;
import com.elice.ustory.domain.grate.repository.GrateRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.recommand.dto.MainRecommendResponse;
import com.elice.ustory.domain.recommand.dto.RecommendCountDTO;
import com.elice.ustory.domain.recommand.dto.RecommendPaperRequest;
import com.elice.ustory.domain.recommand.dto.RecommendPaperResponse;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final AddressRepository addressRepository;
    private final PaperRepository paperRepository;
    private final GrateRepository grateRepository;

    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";

    public List<MainRecommendResponse> getRecommendPapers(Pageable pageable, LocalDateTime requestTime) {

        List<MainRecommendResponse> mainRecommendResponses = new ArrayList<>();

        // Tuple로 가져왔다.
        List<RecommendCountDTO> RecommendCountDTOS = addressRepository.countEqualAddress(pageable, requestTime);

        if (RecommendCountDTOS.isEmpty()) {
            return mainRecommendResponses;
        }

        for (RecommendCountDTO recommendCountDTO : RecommendCountDTOS) {
            MainRecommendResponse mainRecommendResponse = new MainRecommendResponse();
            mainRecommendResponse.setStore(recommendCountDTO.getStore());

            log.info(recommendCountDTO.getStore());

            Address address = new Address(recommendCountDTO);
            List<Paper> papers = paperRepository.joinPaperByAddress(address);

            List<Long> paperIds = new ArrayList<>();

            for (Paper paper : papers) {
                paperIds.add(paper.getId());
            }
            mainRecommendResponse.setPaperIds(paperIds);
            mainRecommendResponse.setImgUrl(papers.get(0).getThumbnailImageUrl());

            mainRecommendResponses.add(mainRecommendResponse);

        }

        return mainRecommendResponses;

    }

    // TODO : 좋아요 갯수 순으로 반납하면 되지 않을까?
    public List<RecommendPaperResponse> getRecommendPaper(RecommendPaperRequest request) {

        List<RecommendPaperResponse> recommendPaperResponses = new ArrayList<>();

        List<Long> paperIds = request.getPaperIds();

        for (Long paperId : paperIds) {
            Paper paper = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));
            RecommendPaperResponse recommendPaperResponse = new RecommendPaperResponse(paper);

            Integer i = grateRepository.countGrateById(paperId);
            recommendPaperResponse.setCountGrate(i);

            recommendPaperResponses.add(recommendPaperResponse);
        }

        return recommendPaperResponses;
    }
}
