package com.elice.ustory.domain.paper.service;

import com.elice.ustory.domain.paper.entity.Image;
import com.elice.ustory.domain.paper.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    public List<Image> createImages(List<Image> images) {
        List<Image> result = new ArrayList<>();

        for (Image image : images) {
            result.add(createImage(image));
        }

        return result;
    }

    public List<Image> findImagesByPaperId(Long paperId) {
        return imageRepository.findByPaperIdOrderBySequenceAsc(paperId);
    }

    @Transactional
    public List<Image> updateImages(Long paperId, List<Image> images) {
        List<Image> savedImages = findImagesByPaperId(paperId);
        List<Image> result = new ArrayList<>();

        updateExistingImages(savedImages, images, result);

        if (savedImages.size() < images.size()) {
            updateNewImages(savedImages.size(), images, result);
        }

        return result;
    }

    private void updateExistingImages(List<Image> savedImages, List<Image> newImages, List<Image> result) {
        for (int i = 0; i < savedImages.size(); i++) {
            Image savedImage = savedImages.get(i);

            if (newImages.size() <= i) {
                imageRepository.delete(savedImage);
                continue;
            }

            result.add(savedImage.update(newImages.get(i).getImageUrl()));
        }
    }

    private void updateNewImages(int savedImageSize, List<Image> newImages, List<Image> result) {
        for (int i = savedImageSize; i < newImages.size(); i++) {
            Image newImage = newImages.get(i);
            result.add(imageRepository.save(newImage));
        }
    }
}
