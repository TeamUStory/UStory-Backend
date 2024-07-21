package com.elice.ustory.domain.recommand;

import com.elice.ustory.domain.address.AddressRecommendDTO;
import com.elice.ustory.domain.address.AddressRepository;
import com.elice.ustory.domain.great.repository.GreatRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.recommand.dto.*;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.redis.recommend.RecommendRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final AddressRepository addressRepository;
    private final PaperRepository paperRepository;
    private final GreatRepository greatRepository;
    private final RecommendRedisService recommendRedisService;

    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";
    private static final String NOT_FOUND_PAPERS = "어떠한 페이퍼도 찾을 수 없습니다.";

    /**
     * 매일 자정마다 추천 페이퍼들을 뽑아낸다. (생명주기 또한 자정까지)
     *
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void setRecommendPapers() {

        recommendRedisService.deleteKeysWithPattern("RecommendPaper*");

        List<RecommendCountDTO> recommendCountDTOS = addressRepository.countEqualAddress();

        if (recommendCountDTOS.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_PAPERS);
        }

        for (RecommendCountDTO recommendCountDTO : recommendCountDTOS) {
            AddressRecommendDTO addressRecommendDTO = new AddressRecommendDTO(recommendCountDTO);
            RecommendRedisDTO recommendRedisDTO = new RecommendRedisDTO();

            recommendRedisDTO.setAddressRecommendDTO(addressRecommendDTO);

            List<Paper> papers = paperRepository.joinPaperByAddress(addressRecommendDTO);

            List<Long> paperIds = new ArrayList<>();

            for (Paper paper : papers) {
                paperIds.add(paper.getId());
            }

            recommendRedisDTO.setPaperIds(paperIds);

            recommendRedisService.saveData(recommendRedisDTO);
        }
    }

    public List<MainRecommendResponse> getRecommendM(int page, int size) {

        List<MainRecommendResponse> mainRecommendResponses = new ArrayList<>();

        int startIndex = (page - 1) * size + 1; // 페이지 번호가 1부터 시작한다고 가정
        int endIndex = page * size;

        for (int i = startIndex; i <= endIndex; i++) {
            MainRecommendResponse mainRecommendResponse = new MainRecommendResponse();
            mainRecommendResponse.setRecommendPaperKey("RecommendPaper" + i);

            RecommendRedisDTO recommendRedisDTO = recommendRedisService.getData("RecommendPaper" + i);

            if (recommendRedisDTO == null) {
                break;
            }

            mainRecommendResponse.setStore(recommendRedisDTO.getAddressRecommendDTO().getStore());

            Paper getPaper = paperRepository.findById(recommendRedisDTO.getPaperIds().get(0))
                    .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, recommendRedisDTO.getPaperIds().get(0))));

            mainRecommendResponse.setImgUrl(getPaper.getThumbnailImageUrl());

            mainRecommendResponses.add(mainRecommendResponse);

        }

        return mainRecommendResponses;

    }

    public List<RecommendPaperResponse> getRecommendPaper(String recommendPaperKey) {

        List<RecommendPaperResponse> recommendPaperResponses = new ArrayList<>();

        List<Long> paperIds = recommendRedisService.getData(recommendPaperKey).getPaperIds();

        for (Long paperId : paperIds) {
            Paper paper = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));
            RecommendPaperResponse recommendPaperResponse = new RecommendPaperResponse(paper);

            Integer countGreatById = greatRepository.countGreatById(paperId);
            recommendPaperResponse.setCountGreat(countGreatById);

            recommendPaperResponses.add(recommendPaperResponse);
        }

        recommendPaperResponses.sort((r1, r2) -> Integer.compare(r2.getCountGreat(), r1.getCountGreat()));

        return recommendPaperResponses;
    }
}
