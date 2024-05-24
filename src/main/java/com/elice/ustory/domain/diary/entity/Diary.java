package com.elice.ustory.domain.diary.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "diary")
@NoArgsConstructor
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(10) not null")
    private String name;

    @Column(name = "diary_img", columnDefinition = "varchar(255) not null")
    private String diaryImg;

    @Column(name = "diary_category", columnDefinition = "varchar(10) not null")
    @Enumerated(EnumType.STRING)
    private DiaryCategory diaryCategory;

    @Column(name = "description", columnDefinition = "varchar(255)")
    private String description;

    public Diary(String name, String diaryImg, DiaryCategory diaryCategory, String description) {
        this.name = name;
        this.diaryImg = diaryImg;
        this.diaryCategory = diaryCategory;
        this.description = description;
    }
}