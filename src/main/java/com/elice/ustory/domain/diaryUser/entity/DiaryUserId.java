package com.elice.ustory.domain.diaryUser.entity;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.user.entity.Users;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryUserId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "diary_id", columnDefinition = "bigint not null")
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "users_id", columnDefinition = "bigint not null")
    private Users users;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiaryUserId) {
            DiaryUserId diaryUserId = (DiaryUserId) obj;
            return this.diary.getId() == diaryUserId.diary.getId() && this.users.getId() == diaryUserId.users.getId();
        } else {
            return false;
        }
    }
}


