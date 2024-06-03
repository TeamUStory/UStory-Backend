package com.elice.ustory.domain.paper;

import com.elice.ustory.domain.bookmark.BookmarkService;
import com.elice.ustory.domain.paper.dto.AddPaperRequest;
import com.elice.ustory.domain.paper.dto.AddPaperResponse;
import com.elice.ustory.domain.paper.dto.PaperCountResponse;
import com.elice.ustory.domain.paper.dto.PaperListResponse;
import com.elice.ustory.domain.paper.dto.PaperMapListResponse;
import com.elice.ustory.domain.paper.dto.PaperResponse;
import com.elice.ustory.domain.paper.dto.UpdatePaperRequest;
import com.elice.ustory.domain.paper.dto.UpdatePaperResponse;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.address.AddressService;
import com.elice.ustory.domain.image.ImageService;
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

@Tag(name = "Paper API")
@RestController
@RequestMapping("/paper")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;
    private final AddressService addressService;
    private final ImageService imageService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "Create Paper API", description = "페이퍼를 생성한다.")
    @PostMapping
    public ResponseEntity<AddPaperResponse> create(@RequestParam Long userId,
                                                   @RequestBody AddPaperRequest addPaperRequest) {

        Paper paper = paperService.createPaper(addPaperRequest.toPageEntity(), userId, addPaperRequest.getDiaryId());

        addressService.create(addPaperRequest.toAddressEntity(), paper);

        imageService.createImages(addPaperRequest.toImagesEntity(), paper);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AddPaperResponse(paper.getId()));
    }

    @Operation(summary = "Update Paper API", description = "페이퍼를 수정한다.")
    @PutMapping("/{paperId}")
    public ResponseEntity<UpdatePaperResponse> update(@PathVariable Long paperId,
                                                      @RequestParam Long userId,
                                                      @RequestBody UpdatePaperRequest updatePaperRequest) {

        Paper paper = paperService.updatePaper(paperId, updatePaperRequest.toPageEntity());

        addressService.update(paper.getAddress().getId(), updatePaperRequest.toAddressEntity());

        imageService.updateImages(paper, updatePaperRequest.toImagesEntity());

        return ResponseEntity.ok(new UpdatePaperResponse(paper.getId()));
    }

    @Operation(summary = "Delete Paper API", description = "페이퍼를 삭제한다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @DeleteMapping("/{paperId}")
    public ResponseEntity<Void> delete(@PathVariable Long paperId,
                                       @RequestParam Long userId) {

        // paperId에 해당하는 paper 삭제
        paperService.deleteById(paperId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Read Paper API", description = "페이퍼를 불러온다.")
    @GetMapping("/{paperId}")
    public ResponseEntity<PaperResponse> getPaper(@PathVariable Long paperId,
                                                  @RequestParam Long userId) {

        Paper paper = paperService.getPaperById(paperId);
        Boolean bookmarked = bookmarkService.isPaperBookmarkedByUser(paperId, userId);

        return ResponseEntity.ok(new PaperResponse(paper, bookmarked));
    }

}
