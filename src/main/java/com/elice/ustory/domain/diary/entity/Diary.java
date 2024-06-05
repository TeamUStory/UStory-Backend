package com.elice.ustory.domain.diary.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.hasText;

@Getter
@Entity
@Table(name = "diary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(40) not null")
    private String name;

    @Column(name = "img_url", columnDefinition = "varchar(1000) not null")
    private String imgUrl;

    @Column(name = "diary_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaryCategory diaryCategory;

    @Column(name = "description", columnDefinition = "varchar(255)")
    private String description;

    @Column(name = "color", nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    public Diary(String name, String imgUrl, DiaryCategory diaryCategory, String description, Color color) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
        this.description = description;
        this.color = color;
    }

    public void updateDiary(Diary diary) {
        if (hasText(diary.getName())) {
            this.name = diary.getName();
        }
        if (hasText(diary.getImgUrl())) {
            this.imgUrl = diary.getImgUrl();
        }
        if (diary.diaryCategory != null) {
            this.diaryCategory = diary.diaryCategory;
        }
        if (hasText(diary.getDescription())) {
            this.description = diary.getDescription();
        }
        if (diary.getColor() != null) {
            this.color = diary.getColor();
        }
    }

    public void updateTime(LocalDateTime now){
        setUpdatedAt(now);
    }
}