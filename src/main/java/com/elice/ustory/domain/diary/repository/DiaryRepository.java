package com.elice.ustory.domain.diary.repository;

import com.elice.ustory.domain.diary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary,Long> {

}