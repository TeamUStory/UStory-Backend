package com.elice.ustory.domain.paper;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.service.DiaryService;
import com.elice.ustory.domain.paper.dto.AddPaperRequest;
import com.elice.ustory.domain.paper.dto.AddPaperResponse;
import com.elice.ustory.domain.paper.dto.PaperListResponse;
import com.elice.ustory.domain.paper.dto.PaperMapListResponse;
import com.elice.ustory.domain.paper.dto.PaperResponse;
import com.elice.ustory.domain.paper.dto.UpdatePaperRequest;
import com.elice.ustory.domain.paper.dto.UpdatePaperResponse;
import com.elice.ustory.domain.paper.entity.Address;
import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.AddressService;
import com.elice.ustory.domain.paper.service.ImageService;
import com.elice.ustory.domain.paper.service.PaperService;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
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

    private final UserService userService;
    private final DiaryService diaryService;
    private final AddressService addressService;
    private final PaperService paperService;
    private final ImageService imageService;

    @Operation(summary = "Create Paper API", description = "페이퍼를 생성한다.")
    @PostMapping("/paper")
    public ResponseEntity<AddPaperResponse> create(@RequestBody AddPaperRequest addPaperRequest) {

        Users user = userService.findById(addPaperRequest.getUserId());

        Diary diary = diaryService.getDiaryById(addPaperRequest.getDiaryId());

        Address address = addressService.createAddress(addPaperRequest.toAddressEntity());

        List<Image> images = imageService.createImages(addPaperRequest.toImagesEntity());

        Paper paper = paperService.createPaper(addPaperRequest.toPageEntity(), images, address, user, diary);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AddPaperResponse(paper.getId()));
    }

    @Operation(summary = "Update Paper API", description = "페이퍼를 수정한다.")
    @PutMapping("/paper/{paperId}")
    public ResponseEntity<UpdatePaperResponse> update(@PathVariable Long paperId,
                                                      @RequestBody UpdatePaperRequest updatePaperRequest) {

        Paper paper = paperService.getPaperById(paperId);

        diaryService.getDiaryById(paper.getDiary().getId());

        Address address = addressService.updateAddress(paper.getAddress().getId(), updatePaperRequest.toAddressEntity());

        List<Image> images = imageService.updateImages(paperId, updatePaperRequest.toImagesEntity());

        Paper result = paperService.updatePaper(paperId, updatePaperRequest.toPageEntity(), images, address);

        return ResponseEntity.ok(new UpdatePaperResponse(result.getId()));
    }

    @Operation(summary = "Read Paper API", description = "페이퍼를 불러온다.")
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<PaperResponse> getPaper(@PathVariable Long paperId) {

        Paper paper = paperService.getPaperById(paperId);

        return ResponseEntity.ok(new PaperResponse(paper));
    }

    @Operation(summary = "Read Papers By User API", description = "유저가 작성한 페이퍼 리스트를 불러온다.")
    @GetMapping(value = "/papers/user", params = "userId")
    public ResponseEntity<List<PaperListResponse>> getAllPapersByUser(@RequestParam(name = "userId") Long userId,
                                                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                                                      @RequestParam(name = "size", defaultValue = "20") int size) {

        // user 검증
        // user 연관된 모든 paper 불러오기

        List<Paper> papers = paperService.getPapersByWriterId(userId, page, size);

        List<PaperListResponse> result = papers.stream()
                .map(PaperListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Read Papers By Diary API", description = "다이어리에 포함된 페이퍼 리스트를 불러온다.")
    @GetMapping(value = "/papers/diary", params = "diaryId")
    public ResponseEntity<List<PaperListResponse>> getAllPagesByDiary(@RequestParam(name = "diaryId") Long diaryId,
                                                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                                                      @RequestParam(name = "size", defaultValue = "20") int size,
                                                                      @RequestParam(name = "startDate", required = false) String startDate,
                                                                      @RequestParam(name = "endDate", required = false) String endDate) {

        // diary 검증
        // diary 연관된 모든 paper 불러오기

        List<Paper> papers = paperService.getPapersByDiaryId(diaryId, page, size);

        List<PaperListResponse> result = papers.stream()
                .map(PaperListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Read Papers for Map API", description = "유저와 관련된 모든 리스트를 불러온다.")
    @GetMapping(value = "/papers/map", params = "userId")
    public ResponseEntity<List<PaperMapListResponse>> getAllPapersForMap(@RequestParam(name = "userId") Long userId) {

        List<Paper> papers = paperService.getPapersByUserId(userId);

        List<PaperMapListResponse> response = papers.stream()
                .map(PaperMapListResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete Paper API", description = "페이퍼를 삭제한다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @DeleteMapping("/paper/{paperId}")
    public ResponseEntity<Void> delete(@PathVariable Long paperId) {

        // paperId에 해당하는 paper 삭제
        paperService.deleteById(paperId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Count Write Paper By Specific User API", description = "특정 유저가 작성한 모든 페이퍼의 갯수를 불러온다.")
    @GetMapping("/paper/count/{userId}")
    public ResponseEntity<Integer> countPaper(@PathVariable(name = "userId") Long userId) {
        Integer count = paperService.countPapersByWriterId(userId);
        return ResponseEntity.ok(count);
    }
}
