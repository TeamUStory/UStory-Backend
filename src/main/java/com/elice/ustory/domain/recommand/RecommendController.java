package com.elice.ustory.domain.recommand;

import com.elice.ustory.domain.paper.dto.PaperListResponse;
import com.elice.ustory.domain.recommand.dto.MainRecommendResponse;
import com.elice.ustory.domain.recommand.dto.RecommendPaperResponse;
import com.elice.ustory.global.Validation.PageableValidation;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @Operation(summary = "Get Recommend Store", description = "추천하는 장소 리스트들을 보여줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MainRecommendResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<MainRecommendResponse>> getMainRecommend(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                        @RequestParam(name = "size", defaultValue = "20") int size) {

        PageableValidation.pageValidate(page, size);
        List<MainRecommendResponse> recommendPapers = recommendService.getRecommendM(page, size);

        return ResponseEntity.ok(recommendPapers);
    }

    @Operation(summary = "Get Recommend Papers", description = "추천하는 장소에 따른 페이퍼들을 보여줍니다. <br> 좋아요의 개수 순으로 반환됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RecommendPaperResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/papers")
    public ResponseEntity<List<RecommendPaperResponse>> getRecommendPaper(@RequestParam(name = "recommendPaperKey") String recommendPaperKey) {
        List<RecommendPaperResponse> recommendPaper = recommendService.getRecommendPaper(recommendPaperKey);
        return ResponseEntity.ok(recommendPaper);
    }

    @Operation(summary = "Reset Recommend Redis DataBase", description = "새로운 추천 리스트들을 보여줍니다. <br> 원래는 자정마다 변경됩니다. <br> 실행시키면 주소 개수에 따라 추천이 변경됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/reset")
    public ResponseEntity<Void> recommendTest() {
        recommendService.setRecommendPapers();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
