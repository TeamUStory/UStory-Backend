package com.elice.ustory.domain.user.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Setter //TODO: 수동 세터로 변경
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_type", columnDefinition = "VARCHAR(255) DEFAULT 'BASIC'")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "email", columnDefinition = "VARCHAR(30) UNIQUE")
    private String email;

    @Column(name = "name", columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(unique = true, columnDefinition = "VARCHAR(10) UNIQUE")
    private String nickname;
    //TODO: 닉네임 제한 다시 걸기(sql injection 대비. 허용되지 않은 값은 제외하고 받아오도록.)

    @Column(name = "password", columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(name = "profile_img_url", columnDefinition = "TEXT")
    private String profileImgUrl;

    @Column(name = "profile_description", columnDefinition = "VARCHAR(150)")
    private String profileDescription;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt;

    public enum LoginType {
        BASIC,
        KAKAO,
        NAVER,
        GOOGLE
    }

    @Builder(builderMethodName = "addUserBuilder")
    public Users(String email, LoginType loginType, String name, String nickname, String password, String profileImgUrl, String profileDescription) {
        this.email = email;
        this.loginType = loginType;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.profileDescription = profileDescription;
    }
}
