package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.address.AddressRepository;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.repository.CommentRepository;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.image.ImageRepository;
import com.elice.ustory.domain.notice.dto.NoticeRequest;
import com.elice.ustory.domain.notice.service.NoticeService;
import com.elice.ustory.domain.paper.dto.AddPaperRequest;
import com.elice.ustory.domain.paper.dto.UpdatePaperRequest;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.ForbiddenException;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperService {

    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";
    private static final String NOT_FOUND_DIARY_MESSAGE = "%d: 해당하는 다이어리가 존재하지 않습니다.";
    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";

    private final PaperRepository paperRepository;
    private final AddressRepository addressRepository;
    private final ImageRepository imageRepository;
    private final NoticeService noticeService;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Paper create(Long writerId, AddPaperRequest request) {

        // Paper 객체 생성
        Paper paper = request.toPaperEntity();

        // Writer 주입
        Users writer = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER_MESSAGE, writerId)));
        paper.addWriter(writer);

        // Diary 주입
        Diary diary = diaryRepository.findById(request.getDiaryId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, request.getDiaryId())));
        paper.addDiary(diary);

        // 개인 다이어리인 경우 Paper 해금상태로 변경
        if (diary.getDiaryCategory().getName().equals("개인")) {
            paper.unLock();
        }

        // Paper 객체 저장
        paper = paperRepository.save(paper);

        // Address 객체 생성 및 저장
        Address address = request.toAddressEntity();
        address.setPaper(paper);
        addressRepository.save(address);

        // Images 객체 생성 및 저장
        List<Image> images = request.toImagesEntity();
        for (Image image : images) {
            image.setPaper(paper);
            imageRepository.save(image);
        }

        // 작성자 Comment 저장
        Comment commentEntity = Comment.addCommentBuilder()
                .paper(paper)
                .content(request.getWriterComment())
                .user(writer)
                .build();
        commentRepository.save(commentEntity);

        // Comment 작성 알림 전송
        needCommentNotice(diary, paper);

        return paper;
    }

    public Paper getPaperById(long Id) {
        return validatePaper(Id);
    }

    @Transactional
    public Paper update(Long userId, Long paperId, UpdatePaperRequest request) {

        // Paper 검증 및 불러오기
        Paper paper = validatePaper(paperId);

        // User 검증
        Users findUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        // User가 Diary에 속하는 User인지 검증
        List<String> findDiaryByUserId = diaryUserRepository.findUserByDiary(paper.getDiary().getId());
        if (!findDiaryByUserId.contains(findUser.getNickname())) {
            throw new ForbiddenException("해당 다이어리에 속해 있는 사용자가 아닙니다.");
        }

        // Paper 업데이트
        paper.update(
                request.getTitle(),
                request.getThumbnailImageUrl(),
                request.getVisitedAt()
        );

        // Address 업데이트
        Address address = paper.getAddress();
        address.update(
                request.getCity(),
                request.getStore(),
                request.getCoordinateX(),
                request.getCoordinateY()
        );

        List<Image> newImages = request.toImagesEntity();
        List<Image> savedimages = paper.getImages();

        int newImageSize = newImages.size();
        int savedImageSize = savedimages.size();

        // Images 업데이트 (1/3) 덮어씌우기
        for (int i = 0; i < newImageSize; i++) {
            if (i == savedImageSize) break;
            Image image = savedimages.get(i);
            image.update(newImages.get(i).getImageUrl());
        }

        // Images 업데이트 (2/3) 추가로 저장하기
        for (int i = savedImageSize; i < newImageSize; i++) {
            Image image = newImages.get(i);
            image.setPaper(paper);
            imageRepository.save(image);
        }

        // Images 업데이트 (3/3) 남는다면 삭제하기
        for (int i = savedImageSize - 1; i >= newImageSize; i--) {
            Image image = savedimages.remove(i);
            imageRepository.delete(image);
        }

        return paper;
    }

    /**
     * 다이어리 내에 존재하는 Papers 최신순으로 페이지네이션
     */
    public List<Paper> getPapersByDiaryId(Long diaryId, int page, int size, LocalDate startDate, LocalDate endDate, LocalDateTime requestTime) {

        // 다이어리 검증
        diaryRepository.findById(diaryId).orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId)));

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return paperRepository.findAllByDiaryIdAndDateRange(diaryId, requestTime, pageRequest, startDate, endDate);
    }

    /**
     * 작성한 Papers 최신순으로 페이지네이션
     */
    public List<Paper> getPapersByWriterId(Long writerId, int page, int size, LocalDateTime requestTime) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return paperRepository.findByWriterId(writerId, requestTime, pageRequest);
    }

    /**
     * 사용자가 속해있는 다이어리의 모든 Paper 불러오기
     */
    public List<Paper> getPapersByUserId(Long userId) {
        return paperRepository.findAllPapersByUserId(userId);
    }

    public void deleteById(Long userId, Long paperId) {

        Paper findPaper = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException(NOT_FOUND_PAPER_MESSAGE));

        if (findPaper.getWriter().getId().equals(userId)) {
            findPaper.softDelete();
        } else {
            throw new ForbiddenException("페이퍼는 작성자만 지울 수 있습니다.");
        }
    }

    public Paper validatePaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        if (paper.isDeleted()) {
            throw new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId));
        }

        return paper;
    }

    public Integer countPapersByWriterId(Long userId) {
        List<Paper> findByWriterId = paperRepository.findByWriterId(userId);
        return findByWriterId.size();
    }

    // 작성자를 제외한 멤버들에게 코멘트를 달아달라고 알림 전송
    public void needCommentNotice(Diary diary, Paper paper) {
//         TODO : 다이어리에 속한 멤버들 전체 다 가져오기
        List<String> userFindByDiary = diaryUserRepository.findUserByDiary(diary.getId());

//         속한 멤버들 중 작성자 제거하기
        userFindByDiary.remove(paper.getWriter().getNickname());

        // 작성자를 제외한 남은 멤버가 들어가있는 다이어리-유저 리스트에다가 알림 보내기
        if (!userFindByDiary.isEmpty()) {
            for (String nickName : userFindByDiary) {
                NoticeRequest noticeRequest = NoticeRequest.builder()
                        .responseId(userRepository.findByNickname(nickName).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다.")).getId())
                        .paperId(paper.getId())
                        .messageType(2)
                        .build();
                noticeService.sendNotice(noticeRequest);
            }
        }
    }

    @Transactional
    public void noticeLocked(Diary diary, Paper paper) {

        if (paper.isUnlocked()) {
            return;
        }

        // 체크된 페이퍼에서 모든 코멘트를 불러온다.
        List<Comment> comments = paper.getComments();

        // Id 중복 체크를 위한 함수를 만든 뒤, 스트림으로 채워준다.
        Set<Long> userIds = comments.stream()
                .map(comment -> comment.getUser().getId())
                .collect(Collectors.toSet());

        // TODO : 다이어리에 속한 유저 List를 가져온다. 현재 다이어리에서 유저를 불러올 수 없기 때문에 주석 처리.
        List<String> userFindByDiary = diaryUserRepository.findUserByDiary(diary.getId());

        // TODO : 다이어리에 속한 유저 List와 set 사이즈 비교 후, 일치하면 바꾸고 노티스 던진다. 현재 다이어리에서 유저를 불러올 수 없기 때문에 주석 처리.
        if (userIds.size() == userFindByDiary.size()) {
            paper.unLock();
            if (userIds.iterator().hasNext()) {
                for (Long userId : userIds) {
                    NoticeRequest noticeRequest = NoticeRequest.builder()
                            .responseId(userId)
                            .paperId(paper.getId())
                            .messageType(4)
                            .build();
                    noticeService.sendNotice(noticeRequest);
                }
            }
        }
    }

}