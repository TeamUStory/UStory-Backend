package com.elice.ustory.domain.paper;

import com.elice.ustory.domain.paper.dto.AddPaperRequest;
import com.elice.ustory.domain.paper.dto.AddPaperResponse;
import com.elice.ustory.domain.paper.dto.PaperListResponse;
import com.elice.ustory.domain.paper.dto.PaperResponse;
import com.elice.ustory.domain.paper.dto.UpdatePaperRequest;
import com.elice.ustory.domain.paper.dto.UpdatePaperResponse;
import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.AddressService;
import com.elice.ustory.domain.paper.service.ImageService;
import com.elice.ustory.domain.paper.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Paper API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaperController {

    private final AddressService addressService;
    private final PaperService paperService;
    private final ImageService imageService;

    @Operation(summary = "Create Paper API", description = "페이퍼를 생성한다.")
    @PostMapping("/paper")
    public ResponseEntity<AddPaperResponse> create(@RequestBody AddPaperRequest addPaperRequest) {

        // 사용자 검증 메서드

        // 다이어리 검증 메서드

        Address address = addressService.createAddress(addPaperRequest.toAddressEntity());

        List<Image> images = imageService.createImages(addPaperRequest.toImagesEntity());

        Paper paper = paperService.createPaper(addPaperRequest.toPageEntity(), images, address);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AddPaperResponse(paper.getId()));
    }

    @Operation(summary = "Update Paper API", description = "페이퍼를 수정한다.")
    @PutMapping("/paper/{paperId}")
    public ResponseEntity<UpdatePaperResponse> update(@PathVariable Long paperId,
                                                      @RequestBody UpdatePaperRequest updatePaperRequest) {

        // 다이어리 검증 메서드 (다이어리에 해당 페이지가 존재하는지 확인)

        // 사용자 검증 메서드 (사용자가 존재하는지, 다이어리에 포함되는지 확인)

        Paper paper = paperService.getPaperById(paperId);

        Address address = addressService.updateAddress(paper.getAddress().getId(), updatePaperRequest.toAddressEntity());

        List<Image> images = imageService.updateImages(paperId, updatePaperRequest.toImagesEntity());

        Paper result = paperService.updatePaper(paperId, updatePaperRequest.toPageEntity(), images, address);

        return ResponseEntity.ok(new UpdatePaperResponse(result.getId()));
    }

    @Operation(summary = "Read Paper API", description = "페이퍼를 불러온다.")
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<PaperResponse> get(@PathVariable Long paperId) {

        Paper paper = paperService.getPaperById(paperId);

        return ResponseEntity.ok(new PaperResponse(paper));
    }

    @Operation(summary = "Read Papers API", description = "모든 페이퍼를 불러온다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @GetMapping("/papers")
    public ResponseEntity<List<PaperListResponse>> getAllPaper(@RequestParam(name = "page", defaultValue = "1") int page,
                                                               @RequestParam(name = "size", defaultValue = "20") int size) {

        List<PaperListResponse> papers = paperService.getAllPapers().stream()
                .map(PaperListResponse::new)
                .toList();

        return ResponseEntity.ok(papers);
    }

    // TODO: userId를 임시로 작성해놨지만 변경 해야함
    @Operation(summary = "Read Papers By User API", description = "유저와 연관된 모든 페이퍼를 불러온다.")
    @GetMapping(value = "/paper/user", params = "userId")
    public ResponseEntity<List<AddPaperResponse>> getAllPapersByUser(@RequestParam(name = "userId") Long userId,
                                                                    @RequestParam(name = "page", defaultValue = "1") int page,
                                                                    @RequestParam(name = "size", defaultValue = "20") int size) {

        // user 검증
        // user 연관된 모든 page 불러오기

        return ResponseEntity.ok(List.of(new AddPaperResponse()));
    }

    @Operation(summary = "Read Papers By Diary API", description = "다이어리와 연관된 모든 페이퍼를 불러온다.")
    @GetMapping(value = "/papers/diary", params = "diaryId")
    public ResponseEntity<List<AddPaperResponse>> getAllPagesByDiary(@RequestParam(name = "diaryId") Long diaryId,
                                                                     @RequestParam(name = "page", defaultValue = "1") int page,
                                                                     @RequestParam(name = "size", defaultValue = "20") int size) {

        // diary 검증
        // diary 연관된 모든 page 불러오기

        return ResponseEntity.ok(List.of(new AddPaperResponse()));
    }

    @Operation(summary = "Delete Paper API", description = "페이퍼를 삭제한다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @DeleteMapping("/paper/{paperId}")
    public ResponseEntity<Void> delete(@PathVariable Long paperId) {

        // paperId에 해당하는 paper 삭제
        paperService.deleteById(paperId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
