package com.elice.ustory.domain.paper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "address")
    private Paper paper;

    @Column(name = "city", nullable = false, columnDefinition = "varchar(30)")
    private String city;

    @Column(name = "store", nullable = false, columnDefinition = "varchar(30)")
    private String store;

    @Column(name = "coordinate_x", nullable = false, columnDefinition = "decimal(15,13)")
    private Double coordinateX;

    @Column(name = "coordinate_y", nullable = false, columnDefinition = "decimal(16,13)")
    private Double coordinateY;

    /**
     * Address 객체 생성자
     *
     * @param city          도로명 주소
     * @param store         상호명
     * @param coordinateX   위도
     * @param coordinateY   경도
     */
    @Builder(builderMethodName = "createBuilder")
    public Address(String city, String store, double coordinateX, double coordinateY) {
        this.city = city;
        this.store = store;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    /**
     * Address 객체 업데이트
     *
     * @param city          도로명 주소
     * @param store         상호명
     * @param coordinateX   위도
     * @param coordinateY   경도
     * @return              업데이트 된 객체
     */
    public Address update(String city, String store, double coordinateX, double coordinateY) {
        this.city = city;
        this.store = store;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;

        return this;
    }

    /**
     * Paper 객체를 지정한다.
     *
     * @param paper         Paper 객체
     */
    public void setPaper(Paper paper) {
        this.paper = paper;
    }
}
