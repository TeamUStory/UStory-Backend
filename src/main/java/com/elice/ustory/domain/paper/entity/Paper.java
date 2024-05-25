package com.elice.ustory.domain.paper.entity;

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
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(name = "thumbnail_image", nullable = false, columnDefinition = "varchar(1000)")
    private String thumbnailImage;

    @OneToMany(
            mappedBy = "paper",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Image> images = new ArrayList<>();

    @Column(name = "visited_at", nullable = false)
    private LocalDate visitedAt;

    // TODO: 다른 엔티티 전부 만들어지면 주석 해제
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private Long writerId;
//
//    @ManyToOne
//    @JoinColumn(name = "diary_id")
//    private Long diaryId;
//
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_ADDRESS_ID"))
    private Address address;
//
//    @OneToMany(mappedBy = "page")
//    private List<Comment> comments;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // TODO: 개발의 편의를 위해 Nullable 가능하도록 바꿈 추후 false 재설정 필요
    @Column(nullable = true, length = 10)
    private String locked;

    // TODO: BaseEntity 만들어지면 주석 해제 후 수정
//    @CreatedDate
//    @Column(name = "created_at", updatable = false, nullable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    @Column(name = "modified_at", nullable = false)
//    private LocalDateTime modifiedAt;

    // TODO: 우선 간단하게 제목, 썸네일, 방문 날짜만 가지고 객체 생성
    @Builder(builderMethodName = "createBuilder")
    public Paper(String title, String thumbnailImage, LocalDate visitedAt) {
        this.title = title;
        this.thumbnailImage = thumbnailImage;
        this.visitedAt = visitedAt;
    }

    public Paper update(String title, String thumbnailImage, LocalDate visitedAt) {
        this.title = title;
        this.thumbnailImage = thumbnailImage;
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

    /**
     * Paper 생성 및 업데이트에 사용
     *
     * @param images 생성되거나 업데이트된 Image List
     */
    public void updateImages(List<Image> images) {

        // 이미지 개수가 동일하면 CascadeType.PERSIST 로 인해 추가할 필요가 없음
        if (this.images.size() == images.size()) {
            return;
        }

        // 이미지 개수가 늘어나 포함되지 않은 이미지 추가하는 로직
        for (Image image : images) {
            if (!this.images.contains(image)) {
                addImage(image);
            }
        }
    }

    private void addImage(Image image) {
        this.images.add(image);

        if (image.getPaper() != this) {
            image.setPaper(this);
        }
    }

    public void setAddress(Address address) {
        this.address = address;

        if (address.getPaper() != this) {
            address.setPaper(this);
        }
    }
}
