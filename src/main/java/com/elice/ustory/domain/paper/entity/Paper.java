package com.elice.ustory.domain.paper.entity;

import com.elice.ustory.domain.address.Address;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "paper", uniqueConstraints = @UniqueConstraint(name = "UK_PAPER_ADDRESS_ID", columnNames = "address_id"))
public class Paper extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(name = "thumbnail_image_url", nullable = false, columnDefinition = "varchar(1000)")
    private String thumbnailImageUrl;

    @OneToMany(
            mappedBy = "paper",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Image> images = new ArrayList<>();

    @Column(name = "visited_at", nullable = false)
    private LocalDate visitedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users writer;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @JoinColumn(name = "address_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_ADDRESS_ID"))
    private Address address;

    @OneToMany(mappedBy = "paper")
    private List<Comment> comments;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // TODO: 개발의 편의를 위해 Nullable 가능하도록 바꿈 추후 false 재설정 필요
    @Column(name = "unlocked", nullable = false, columnDefinition = "tinyint")
    @Setter
    private int unLocked;

    @Builder(builderMethodName = "createBuilder")
    public Paper(String title, String thumbnailImageUrl, LocalDate visitedAt) {
        this.title = title;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.visitedAt = visitedAt;
        this.unLocked = 0;
    }

    public Paper update(String title, String thumbnailImageUrl, LocalDate visitedAt) {
        this.title = title;
        this.thumbnailImageUrl = thumbnailImageUrl;
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
}
