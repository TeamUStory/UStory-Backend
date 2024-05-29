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
@NoArgsConstructor
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_type", columnDefinition = "varchar(255) default 'BASIC'")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(unique = true, name = "email", length = 20)
    private String email;

    @Column(name = "name", length = 10)
    private String name;

    @Column(unique = true, name = "nickname", length = 10)
    private String nickname;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum LoginType {
        BASIC,
        KAKAO,
        NAVER,
        GOOGLE
    }

    @Builder(builderMethodName = "addUserBuilder")
    public Users(String email, LoginType loginType, String name, String nickname, String password, String profileImg) {
        this.email = email;
        this.loginType = loginType;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
    }
}
