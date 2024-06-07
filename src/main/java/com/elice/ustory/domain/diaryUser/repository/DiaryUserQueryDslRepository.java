package com.elice.ustory.domain.diaryUser.repository;

import com.elice.ustory.domain.diary.dto.DiaryFriend;
import com.elice.ustory.domain.diary.dto.DiaryList;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.user.entity.Users;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryUserQueryDslRepository {
    List<DiaryList> searchDiaryList(Long userId);

    Long countUserByDiary(Long diaryId);

    Long countDiaryByUser(Long userId);

    List<String> findUserByDiary(Long diaryId);

    List<Tuple> findUsersByDiary(Long userId, Long diaryId, List<String> userList);

    List<DiaryList> searchDiary(Long userId, Pageable pageable, DiaryCategory diaryCategory, LocalDateTime dateTime, String searchWord);

    DiaryUser findDiaryUserById(Long userId, Long diaryId);

    List<Users> findFriendUsersByList(Long userId, List<String> userList);

    List<DiaryFriend> findUsersByDiaryId(Long userId,Long diaryId);

}
