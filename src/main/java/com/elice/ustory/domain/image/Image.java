package com.elice.ustory.domain.image;

import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
public class Image {

    private static final String PARAMETER_TOO_LONG = "이미지의 %d번째 이미지의 길이가 너무 깁니다.";
    private static final String SUBFIX_NOT_MATCH = "이미지의 확장자를 확인하여 주세요. (.jpg, .jpeg, .png, .gif)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paper_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_PAPER_ID"))
    private Paper paper;

    @Column(name = "image_url", nullable = false, columnDefinition = "varchar(1000)")
    private String imageUrl;

    @Column(name = "sequence", nullable = false, columnDefinition = "tinyint")
    private int sequence;

    public Image(String imageUrl, int sequence) {
        this.imageUrl = validateUrl(validateUrlParam(imageUrl, sequence));
        this.sequence = sequence;
    }

    public Image update(String imageUrl) {
        this.imageUrl = validateUrl(updateValidateParam(imageUrl));
        return this;
    }

    public void setPaper(Paper paper){
        this.paper = paper;

        if(!paper.getImages().contains(this)) {
            paper.getImages().add(this);
        }
    }

    private String validateUrlParam(String validateTarget, int sequence) {
        if (validateTarget.length() > 1000) {
            throw new ValidationException(String.format(PARAMETER_TOO_LONG, sequence), ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        return validateTarget;
    }

    private String updateValidateParam(String validateTarget) {
        if (validateTarget.length() > 1000) {
            throw new ValidationException(PARAMETER_TOO_LONG, ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        return validateTarget;
    }

    private String validateUrl(String validateTarget) {
        String[] suffixes = {".jpg", ".jpeg", ".png", ".gif"};

        if (!validateTarget.startsWith("https://")) {
            throw new ValidationException("이미지의 시작은 https:// 입니다.", ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        if (Stream.of(suffixes).noneMatch(validateTarget::endsWith)) {
            throw new ValidationException(SUBFIX_NOT_MATCH, ErrorCode.PARAMETER_INCORRECT_FORMAT);
        }

        return validateTarget;
    }

}
