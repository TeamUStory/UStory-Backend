package com.elice.ustory.domain.image;

import com.elice.ustory.domain.paper.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public void createImages(List<Image> images, Paper paper) {
        for (Image image : images) {
            createImage(image, paper);
        }
    }

    private Image createImage(Image image, Paper paper) {
        image.setPaper(paper);
        return imageRepository.save(image);
    }

    public void updateImages(Paper paper, List<Image> images) {
        List<Image> savedImages = findImagesByPaperId(paper.getId());

        updateExistingImages(savedImages, images);

        if (savedImages.size() < images.size()) {
            updateNewImages(savedImages.size(), images,  paper);
        }
    }

    private List<Image> findImagesByPaperId(Long paperId) {
        return imageRepository.findByPaperIdOrderBySequenceAsc(paperId);
    }

    private void updateExistingImages(List<Image> savedImages, List<Image> newImages) {
        for (int i = 0; i < savedImages.size(); i++) {
            Image savedImage = savedImages.get(i);

            if (newImages.size() <= i) {
                imageRepository.delete(savedImage);
                continue;
            }

            savedImage.update(newImages.get(i).getImageUrl());
        }
    }

    private void updateNewImages(int savedImageSize, List<Image> newImages, Paper paper) {
        for (int i = savedImageSize; i < newImages.size(); i++) {
            Image newImage = newImages.get(i);
            createImage(newImage, paper);
        }
    }
}
