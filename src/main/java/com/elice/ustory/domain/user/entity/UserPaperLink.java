package com.elice.ustory.domain.user.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.awt.print.Paper;

@Entity
@Getter
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
