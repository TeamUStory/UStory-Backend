package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.dto.DiaryList;
import com.elice.ustory.domain.diary.dto.DiaryListResponse;
import com.elice.ustory.domain.diary.dto.DiaryResponse;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.entity.QDiary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import com.elice.ustory.global.exception.model.ValidationException;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.elice.ustory.domain.user.entity.QUsers.users;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private static final String NOT_FOUND_DIARY_MESSAGE = "%d: 해당하는 다이어리가 존재하지 않습니다.";
    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";
    private static final String UNAUTHORIZED_DIARY_MESSAGE = "%d: 해당 다이어리에 대한 권한이 없습니다.";

    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryResponse createDiary(Long userId, Diary diary, List<String> userList) {
        if(diary.getDiaryCategory()==DiaryCategory.INDIVIDUAL){
            throw new ValidationException("개인 다이어리는 생성할 수 없습니다.");
        }

        Diary savedDiary = diaryRepository.save(diary);

        List<Users> friendList = diaryUserRepository.findFriendUsersByList(userId, userList);
        if(friendList.size()!=userList.size()){
            // 친구가 아닌 인원을 다이어리에 추가할 때
            throw new UnauthorizedException("해당하는 친구가 존재하지 않습니다.");
        }

        for (Users friend : friendList) {
            DiaryUserId diaryUserId = new DiaryUserId(savedDiary, friend);
            diaryUserRepository.save(new DiaryUser(diaryUserId));
        }
        Users user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_USER_MESSAGE, userId))
        );
        DiaryUserId diaryUserId = new DiaryUserId(savedDiary, user);
        diaryUserRepository.save(new DiaryUser(diaryUserId));

        return DiaryResponse.toDiaryResponse(savedDiary);
    }

    public DiaryResponse getDiaryDetailById(Long userId, Long diaryId) {
        DiaryUser diaryUser = diaryUserRepository.findDiaryUserById(userId, diaryId);
        if(diaryUser==null){
            throw new UnauthorizedException(String.format(UNAUTHORIZED_DIARY_MESSAGE, diaryId));
        }

        return DiaryResponse.toDiaryResponse(diaryUser.getId().getDiary());
    }

    public Diary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId))
        );
    }

    @Transactional
    public DiaryResponse updateDiary(Long userId, Long diaryId, Diary diary, List<String> userList) {
        Diary updatedDiary = diaryRepository.findById(diaryId).orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId))
        );
        if(diary.getDiaryCategory()==DiaryCategory.INDIVIDUAL){
            throw new ValidationException("개인 다이어리는 생성할 수 없습니다.");
        }
        updatedDiary.updateDiary(diary);

        // 다이어리에 유저가 추가된 경우
        if (userList.size() >= diaryUserRepository.countUserByDiary(diaryId)) {
            List<Tuple> usersByDiary = diaryUserRepository.findUsersByDiary(userId, diaryId, userList);
            if(usersByDiary.size()>9) {
                // request를 보낸 유저까지 10명을 초과하는 경우
                throw new ValidationException("다이어리 인원을 10명을 초과할 수 없습니다.");

            }else if(usersByDiary.size()<userList.size()){
                // 존재하지 않는 유저 닉네임이 보내진 경우 && 개인 다이어리의 경우(에러 메세지에 대한 분리 및 고민이 필요)
                throw new NotFoundException("해당하는 친구가 존재하지 않습니다.");
            }
            for (Tuple tuple : usersByDiary) {
                Users user = tuple.get(users);
                if(tuple.get(QDiary.diary.id)!=null){
                    if(!userList.contains(user.getNickname())){
                        // 존 유저가 사라진 케이스
                        throw new ValidationException("기존 다이어리의 유저가 모두 포함되지 않습니다.");
                    }
                }else{
                    DiaryUserId diaryUserId = new DiaryUserId(updatedDiary, user);
                    diaryUserRepository.save(new DiaryUser(diaryUserId));
                }
            }
        }

        return DiaryResponse.toDiaryResponse(updatedDiary);
    }

    public List<DiaryListResponse> getUserDiaries(Long userId, Pageable pageable, DiaryCategory diaryCategory, LocalDateTime dateTime) {
        List<DiaryList> diaryList = diaryUserRepository.searchDiary(userId, pageable, diaryCategory, dateTime);
        List<DiaryListResponse> result = diaryList.stream()
                .map(DiaryList::toDiaryListResponse)
                .collect(Collectors.toList());

        return result;
    }

    public List<DiaryListResponse> getUserDiaryList(Long userId) {
        List<DiaryList> result = diaryUserRepository.searchDiaryList(userId);
        return result.stream()
                .map(DiaryList::toDiaryListResponse)
                .collect(Collectors.toList());
    }

    public Long getDiaryCount(Long userId) {
        return diaryUserRepository.countDiaryByUser(userId);
    }

    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId))
        );

        diaryRepository.delete(diary);
    }

    public void exitDiary(Long userId, Long diaryId) {
        DiaryUser diaryUser = diaryUserRepository.findDiaryUserById(userId, diaryId);
        if(diaryUser == null){
            // 사용자가 속한 다이어리가 아닌 경우
            throw new UnauthorizedException(String.format(UNAUTHORIZED_DIARY_MESSAGE, diaryId));
        }

        if (diaryUser.getId().getDiary().getDiaryCategory()!=DiaryCategory.INDIVIDUAL) {
            throw new UnauthorizedException("개인 다이어리는 삭제할 수 없습니다.");
            // TODO : 삭제 후 재생성(?)
        }
        else{
            // 사용자가 속한 다이어리가 아닌 경우
            diaryUserRepository.delete(diaryUser);
        }

        // TODO : diary가 비워진 경우 소프트 딜리트(?) -> 관련 페이퍼는 ?
        if(diaryUserRepository.countUserByDiary(diaryId)==0){

        }
    }

}