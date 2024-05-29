package com.elice.ustory.domain.diaryUser.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary_users")
@Entity
public class DiaryUser extends BaseEntity {
    @EmbeddedId
    DiaryUserId id;

    public DiaryUser(DiaryUserId id) {
        this.id = id;
    }
}
