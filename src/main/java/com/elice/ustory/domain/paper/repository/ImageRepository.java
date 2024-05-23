package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
