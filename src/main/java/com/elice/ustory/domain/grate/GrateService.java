package com.elice.ustory.domain.grate;

import com.elice.ustory.domain.grate.entity.Grate;
import com.elice.ustory.domain.grate.repository.GrateRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.ConflictException;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrateService {

    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";
    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";
    private static final String NOT_FOUND_GREAT_MESSAGE = "해당하는 좋아요가 존재하지 않습니다.";
    private static final String CONFLICT_GREAT_MESSAGE = "이미 좋아요로 지정되어 있습니다.";

    private final GrateRepository grateRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;

    public Grate saveGrate(Long userId, Long paperId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER_MESSAGE, userId)));

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        if (isPaperGratedByUser(userId, paperId)) {
            throw new ConflictException(CONFLICT_GREAT_MESSAGE);
        }

        Grate grate = new Grate(user, paper);
        return grateRepository.save(grate);

    }

    /** 좋아요한 모든 paper 불러오기 */
    public List<Paper> getGratesByUser(Long userId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return grateRepository.findGratesByUserId(userId, pageRequest);
    }

    /** 좋아요 판별 메서드 */
    public boolean isPaperGratedByUser(Long userId, Long paperId) {
        return grateRepository.existsByUserIdAndPaperId(userId, paperId);
    }

    /** 좋아요 삭제 메서드 */
    public void deleteGrate(Long userId, Long paperId) {
        Grate grate = grateRepository.findByUserIdAndPaperId(userId, paperId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_GREAT_MESSAGE));

        grateRepository.delete(grate);
    }

    /** 좋아요 총 개수 반환 메서드 **/
    public int countGratedById(Long paperId) {

        // 먼저 해당 페이퍼가 있는지 없는지 체크한 뒤, 페이퍼가 없다면 에러 반환
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        return grateRepository.countGrateById(paperId);
    }
}
