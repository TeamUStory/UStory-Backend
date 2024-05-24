package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
