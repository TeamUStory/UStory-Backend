package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.user.entity.Users;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryDto {
    @NotNull
    private String name;

    @NotNull
    private String diaryImg;

    @NotNull
    private DiaryCategory diaryCategory;

    @NotNull
    @Size(max = 20)
    private String description;

    @NotNull
    private Color color;

    @NotEmpty
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
