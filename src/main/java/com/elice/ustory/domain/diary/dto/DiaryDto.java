package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DiaryDto {
    private String name;

    private String diaryImg;

    private DiaryCategory diaryCategory;

    private String description;

    private Color color;

    private List<String> users;

    public DiaryDto(String name, String diaryImg, DiaryCategory diaryCategory, String description, Color color, List<String> users) {
        this.name = name;
        this.diaryImg = diaryImg;
        this.diaryCategory = diaryCategory;
        this.description = description;
        this.color = color;
        this.users = users;
    }

    public Diary toDiary() {
        Diary diary = new Diary(name, diaryImg, diaryCategory, description, color);
        return diary;
    }
}
