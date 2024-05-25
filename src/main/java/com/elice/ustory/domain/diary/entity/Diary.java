package com.elice.ustory.domain.diary.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "diary")
@NoArgsConstructor
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(10) not null")
    private String name;

    @Column(name = "img_url", columnDefinition = "varchar(255) not null")
    private String imgUrl;

    @Column(name = "diary_category",nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaryCategory diaryCategory;

    @Column(name = "description", columnDefinition = "varchar(255)")
    private String description;

    @Column(name="color", nullable = false)
    private Color color;

    public Diary(String name, String imgUrl, DiaryCategory diaryCategory, String description) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
        this.description = description;
    }

    public void updateDiary(Diary diary){
        if(diary.getName()!=null){
            this.name=diary.getName();
        }
        if(diary.getImgUrl()!=null){
            this.imgUrl=diary.getImgUrl();
        }
        if(diary.diaryCategory!=null){
            this.diaryCategory=diary.diaryCategory;
        }
        if(diary.getDescription()!=null){
            this.description=diary.getDescription();
        }
        if(diary.getColor()!=null){
            this.color=diary.getColor();
        }
    }
}