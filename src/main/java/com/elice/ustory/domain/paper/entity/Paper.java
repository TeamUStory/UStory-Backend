package com.elice.ustory.domain.paper.entity;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.global.entity.BaseEntity;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.ValidationException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "paper", uniqueConstraints = @UniqueConstraint(name = "UK_PAPER_ADDRESS_ID", columnNames = "address_id"))
public class Paper extends BaseEntity {

    private static final String PARAMETER_TOO_LONG = "%s: 해당 파라미터의 길이가 너무 깁니다.";
    private static final String WRONG_PARAMETER_FORMAT = "%s: 해당 파라미터의 형식이 잘못 되었습니다. (한글, 영어, 숫자, 특수문자를 확인하세요.)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 50)
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @Column(name = "thumbnail_image_url", nullable = false, columnDefinition = "VARCHAR(1000)")
    private String thumbnailImageUrl;

    @OneToMany(
            mappedBy = "paper",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Image> images = new ArrayList<>();

    @Column(name = "visited_at", nullable = false, columnDefinition = "DATE")
    private LocalDate visitedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users writer;

    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @JoinColumn(name = "address_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_ADDRESS_ID"))
    private Address address;

    @OneToMany(mappedBy = "paper")
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "deleted_at", columnDefinition = "DATETIME")
    private LocalDateTime deletedAt;

    @Column(name = "unlocked", nullable = false, columnDefinition = "tinyint")
    @Setter
    private int unLocked;

    @Builder(builderMethodName = "createBuilder")
    public Paper(String title, String thumbnailImageUrl, LocalDate visitedAt) {
        this.title = validateParam(title, 20, "^[가-힣a-zA-Z0-9\\s]+$", "타이틀");
        this.thumbnailImageUrl = validateUrl(validateParam(thumbnailImageUrl, 1000, "^.+$", "썸네일"));
        this.visitedAt = visitedAt;
        this.unLocked = 0;
    }

    public Paper update(String title, String thumbnailImageUrl, LocalDate visitedAt) {
        this.title = validateParam(title, 20, "^[가-힣a-zA-Z0-9]+$", "타이틀");
        this.thumbnailImageUrl = validateParam(thumbnailImageUrl, 1000, "^.+$", "썸네일");
        this.visitedAt = visitedAt;

        return this;
    }

    public boolean softDelete() {

        if (deletedAt != null) {
            return false;
        }

        deletedAt = LocalDateTime.now();
        return true;
    }

    public boolean isDeleted() {

        if (deletedAt != null) {
            return true;
        }

        return false;
    }

    public boolean unLock() {

        if (this.unLocked == 0) {
            this.unLocked = 1;
            return true;
        }

        return false;
    }

    public boolean isUnlocked() {

        if (this.unLocked == 0) {
            return false;
        }

        return true;
    }

    public void addWriter(Users writer) {

        if (this.writer != null) {
            return;
        }

        this.writer = writer;
    }

    public void addDiary(Diary diary) {

        if (this.diary != null) {
            return;
        }

        this.diary = diary;
    }

    public void setAddress(Address address) {
        this.address = address;

        if (address.getPaper() != this) {
            address.setPaper(this);
        }
    }

    private String validateParam(String validateTarget, int size, String regex, String fieldName) {
        if (validateTarget.length() > size) {
            throw new ValidationException(String.format(PARAMETER_TOO_LONG, fieldName), ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        if (!validateTarget.matches(regex)) {
            throw new ValidationException(String.format(WRONG_PARAMETER_FORMAT, fieldName), ErrorCode.PARAMETER_INCORRECT_FORMAT);
        }

        return validateTarget;
    }

    private String validateUrl(String validateTarget) {
        String[] suffixes = {".jpg", ".jpeg", ".png", ".gif"};

        if (!validateTarget.startsWith("https://")) {
            throw new ValidationException("썸네일이 특정 문자로 시작하지 않습니다.", ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        if (Stream.of(suffixes).noneMatch(validateTarget::endsWith)) {
            throw new ValidationException("썸네일의 확장자를 확인해주세요. (.jpg, .jpeg, .png, .gif)", ErrorCode.PARAMETER_INCORRECT_FORMAT);
        }

        return validateTarget;
    }
}
