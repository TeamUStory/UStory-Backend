package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.dto.*;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.entity.QDiary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.ForbiddenException;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.ValidationException;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.elice.ustory.domain.user.entity.QUsers.users;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private static final String NOT_FOUND_DIARY_MESSAGE = "%d: 해당하는 다이어리가 존재하지 않습니다.";
    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";
    private static final String FORBIDDEN_DIARY_MESSAGE = "%d: 해당 다이어리에 대한 권한이 없습니다.";

    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddDiaryResponse createDiary(Long userId, Diary diary, List<String> userList) {
        if (diary.getDiaryCategory() == DiaryCategory.INDIVIDUAL) {
            throw new ValidationException("개인 다이어리는 생성할 수 없습니다.");
        }

        Diary savedDiary = diaryRepository.save(diary);

        List<Users> friendList = diaryUserRepository.findFriendUsersByList(userId, userList);
        if (friendList.size() != userList.size()) {
            // 친구가 아닌 인원을 다이어리에 추가할 때
            throw new ValidationException("해당하는 친구가 존재하지 않습니다.");
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

        List<DiaryFriend> diaryFriends = new ArrayList<>();
        for (Users users : friendList) {
            diaryFriends.add(new DiaryFriend(users.getNickname(),users.getProfileImgUrl()));
        }
        diaryFriends.add(new DiaryFriend(user.getNickname(),user.getProfileImgUrl()));

        return new AddDiaryResponse(savedDiary.getId());
    }

    public DiaryResponse getDiaryDetailById(Long userId, Long diaryId) {
        DiaryUser diaryUser = diaryUserRepository.findDiaryUserById(userId, diaryId);
        if (diaryUser == null) {
            throw new ForbiddenException(String.format(FORBIDDEN_DIARY_MESSAGE, diaryId));
        }

        List<DiaryFriend> diaryFriends = diaryUserRepository.findUsersByDiaryId(userId, diaryId);

        return DiaryResponse.toDiaryResponse(diaryUser.getId().getDiary(), diaryFriends);
    }

    public Diary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_DIARY_MESSAGE, diaryId))
        );
    }

    @Transactional
    public AddDiaryResponse updateDiary(Long userId, Long diaryId, Diary diary, List<String> userList) {
        DiaryUser diaryUser = diaryUserRepository.findDiaryUserById(userId, diaryId);
        if (diaryUser == null) {
            // 사용자가 속한 다이어리가 아닌 경우
            throw new ForbiddenException(String.format(FORBIDDEN_DIARY_MESSAGE, diaryId));
        }

        Diary updatedDiary = diaryUser.getId().getDiary();
        if (updatedDiary.getDiaryCategory() == DiaryCategory.INDIVIDUAL) {
            if (diary.getDiaryCategory() != DiaryCategory.INDIVIDUAL) {
                throw new ValidationException("개인 다이어리의 카테고리는 변경할 수 없습니다.");
            }
            if (userList.size() > 1) {
                throw new ValidationException("개인 다이어리는 인원이 추가될 수 없습니다.");
            }
        }
        updatedDiary.updateDiary(diary);

        // 다이어리에 유저가 추가된 경우
        if (userList.size() >= diaryUserRepository.countUserByDiary(diaryId)) {
            List<Tuple> usersByDiary = diaryUserRepository.findUsersByDiary(userId, diaryId, userList);
            if (usersByDiary.size() > 9) {
                // request를 보낸 유저까지 10명을 초과하는 경우
                throw new ValidationException("다이어리 인원을 10명을 초과할 수 없습니다.");
            } else if (usersByDiary.size() < userList.size() - 1) {
                // 존재하지 않는 유저 닉네임이 보내진 경우
                throw new NotFoundException("해당하는 친구가 존재하지 않습니다.");
            }
            for (Tuple tuple : usersByDiary) {
                Users user = tuple.get(users);
                if (tuple.get(QDiary.diary.id) != null) {
                    if (!userList.contains(user.getNickname())) {
                        // 존 유저가 사라진 케이스
                        throw new ValidationException("기존 다이어리의 유저가 모두 포함되지 않습니다.");
                    }
                } else {
                    DiaryUserId diaryUserId = new DiaryUserId(updatedDiary, user);
                    diaryUserRepository.save(new DiaryUser(diaryUserId));
                }
            }
        }

        return new AddDiaryResponse(diaryId);
    }

    public List<DiaryListResponse> getUserDiaries(Long userId, Pageable pageable, DiaryCategory diaryCategory, LocalDateTime dateTime, String searchWord) {
        if(!hasText(searchWord)) searchWord = null;
        List<DiaryList> diaryList = diaryUserRepository.searchDiary(userId, pageable, diaryCategory, dateTime, searchWord);
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

    public ExitResponse exitDiary(Long userId, Long diaryId) {
        DiaryUser diaryUser = diaryUserRepository.findDiaryUserById(userId, diaryId);
        if (diaryUser == null) {
            // 사용자가 속한 다이어리가 아닌 경우
            throw new ForbiddenException(String.format(FORBIDDEN_DIARY_MESSAGE, diaryId));
        }

        if (diaryUser.getId().getDiary().getDiaryCategory() != DiaryCategory.INDIVIDUAL) {
            return new ExitResponse(false);
        } else {
            diaryUserRepository.delete(diaryUser);
        }

        return new ExitResponse(true);
    }

}