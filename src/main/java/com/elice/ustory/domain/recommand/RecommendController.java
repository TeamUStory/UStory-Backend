package com.elice.ustory.domain.recommand;

import com.elice.ustory.domain.recommand.dto.MainRecommendResponse;
import com.elice.ustory.domain.recommand.dto.RecommendPaperRequest;
import com.elice.ustory.domain.recommand.dto.RecommendPaperResponse;
import com.elice.ustory.global.Validation.PageableValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<List<MainRecommendResponse>> getMainRecommend(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                        @RequestParam(name = "size", defaultValue = "20") int size,
                                                                        @RequestParam(name = "requestTime") LocalDateTime requestTime) {

        Pageable pageable = PageableValidation.madePageable(page, size);
        List<MainRecommendResponse> recommendPapers = recommendService.getRecommendPapers(pageable, requestTime);

        return ResponseEntity.ok(recommendPapers);
    }

    @PostMapping("/papers")
    public ResponseEntity<List<RecommendPaperResponse>> getRecommendPaper(@RequestBody RecommendPaperRequest request) {
        List<RecommendPaperResponse> recommendPaper = recommendService.getRecommendPaper(request);
        return ResponseEntity.ok(recommendPaper);
    }

}
