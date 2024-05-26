package com.elice.ustory.domain.comment.entity;

import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @Builder(builderMethodName = "addCommentBuilder")
    public Comment(String content, Paper paper) {
        this.content = content;
        this.paper = paper;
    }

    public Comment update(String content) {
        this.content = content;
        return this;
    }
}
