package com.elice.ustory.domain.user.entity;

import com.elice.ustory.global.entity.BaseEntity;
import com.elice.ustory.domain.paper.entity.Paper;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_paper_link")
public class UserPaperLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @Builder(builderMethodName = "addUserPaperBuilder")
    public UserPaperLink(Users user, Paper paper) {
        this.user = user;
        this.paper = paper;
    }

}
