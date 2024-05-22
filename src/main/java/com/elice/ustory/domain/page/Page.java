package com.elice.ustory.domain.page;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ustory.ustory_service.domain.comment.entity.Comment;

// TODO: 엔티티 ERD 참조하여 작성
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "page")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    // TODO : 이거 있지 않았나? 페이지에? 메인 사진은 따로 두고, 사진들 URL 저장을 여기다 하는거 아니었나? 일단 먼저 추가합니다. 나머지는 알려주셔야 할 것 같습니다 기중느임
    @Column(name = "main_image_url", nullable = false, columnDefinition = "varchar(1000)")
    private String mainImageURI;

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
//    @ManyToOne
//    @JoinColumn(name = "address_id")
//    private Long addressId;
//
//    @OneToMany(mappedBy = "page")
//    private List<Comment> comments;

    // TODO : 이거 세터 써도 됩니까? 빌더 써야하나요?
    @Column(name = "deleted_at")
    @Setter
    private LocalDateTime deletedAt;

    @Column(nullable = false, length = 10)
    private String locked;

    // TODO: BaseEntity 만들어지면 주석 해제 후 수정
//    @CreatedDate
//    @Column(name = "created_at", updatable = false, nullable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    @Column(name = "modified_at", nullable = false)
//    private LocalDateTime modifiedAt;

}
