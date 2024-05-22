package com.elice.ustory.domain.diary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "diary")
@NoArgsConstructor
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "diary_img", nullable = false)
    private String diaryImg;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiaryCategory diaryCategory;

    public Diary(String name, String diaryImg, DiaryCategory diaryCategory) {
        this.name = name;
        this.diaryImg = diaryImg;
        this.diaryCategory = diaryCategory;
    }
}
