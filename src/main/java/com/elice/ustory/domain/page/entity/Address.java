package com.elice.ustory.domain.page.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Page page;

    @Column(nullable = false, length = 30)
    private String city;

    @Column(nullable = false, length = 30)
    private String detail;

    @Column(nullable = false, length = 30)
    private String store;

    @Column(nullable = false)
    private float coordinateX;

    @Column(nullable = false)
    private float coordinateY;

    // 주소 생성자
    @Builder(builderMethodName = "createBuilder")
    public Address(String city, String detail, String store, float coordinateX, float coordinateY) {
        this.city = city;
        this.detail = detail;
        this.store = store;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    // 주소 정보 업데이트
    public Address update(String city, String detail, String store, float coordinateX, float coordinateY) {
        this.city = city;
        this.detail = detail;
        this.store = store;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;

        return this;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
