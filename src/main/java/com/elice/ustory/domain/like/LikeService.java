package com.elice.ustory.domain.like;

import com.elice.ustory.domain.like.entity.Like;
import com.elice.ustory.domain.like.repository.LikeRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.ConflictException;
import com.elice.ustory.global.exception.model.InternalServerException;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";
    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";
    private static final String NOT_FOUND_LIKE_MESSAGE = "해당하는 좋아요가 존재하지 않습니다.";
    private static final String CONFLICT_LIKE_MESSAGE = "이미 좋아요로 지정되어 있습니다.";
    private static final String MINUS_LIKE_MESSAGE = "%d: 현재 페이퍼의 좋아요에서 문제가 발생했습니다.(음수)";

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;

    public Like saveLike(Long userId, Long paperId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER_MESSAGE, userId)));

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        if (isPaperLikedByUser(userId, paperId)) {
            throw new ConflictException(CONFLICT_LIKE_MESSAGE);
        }

        Like like = new Like(user, paper);
        return likeRepository.save(like);

    }

    /** 좋아요한 모든 paper 불러오기 */
    public List<Paper> getLikesByUser(Long userId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return likeRepository.findLikesByUserId(userId, pageRequest);
    }

    /** 좋아요 판별 메서드 */
    public boolean isPaperLikedByUser(Long userId, Long paperId) {
        return likeRepository.existsByUserIdAndPaperId(userId, paperId);
    }

    /** 좋아요 삭제 메서드 */
    public void deleteLike(Long userId, Long paperId) {
        Like like = likeRepository.findByUserIdAndPaperId(userId, paperId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_LIKE_MESSAGE));

        likeRepository.delete(like);
    }

    /** 좋아요 총 갯수 반환 메서드 **/
    public int countLikedById(Long paperId) {

        // 먼저 해당 페이퍼가 있는지 없는지 체크한 뒤, 페이퍼가 없다면 에러 반환
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        Integer count = likeRepository.countLikeById(paperId);

        if(count < 0) {
            throw new InternalServerException(String.format(MINUS_LIKE_MESSAGE, paperId));
        }

        return count;
    }
}
