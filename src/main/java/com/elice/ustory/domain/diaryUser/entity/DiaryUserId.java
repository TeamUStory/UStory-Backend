package com.elice.ustory.domain.diaryUser.entity;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.user.entity.Users;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Getter
@Embeddable
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryUserId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", columnDefinition = "bigint not null")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", columnDefinition = "bigint not null")
    private Users users;

}


