package com.elice.ustory.domain.page.service;

import com.elice.ustory.domain.page.entity.Image;
import com.elice.ustory.domain.page.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    @Transactional
    public List<Image> createImages(List<Image> images) {
        for (Image image : images) {
            imageRepository.save(image);
        }

        return images;
    }
}
