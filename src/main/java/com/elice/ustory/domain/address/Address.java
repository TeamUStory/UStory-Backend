package com.elice.ustory.domain.address;

import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.global.exception.ErrorCode;
import com.elice.ustory.global.exception.model.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "address")
public class Address {

    private static final String PARAMETER_TOO_LONG = "%s: 해당 파라미터의 길이가 너무 깁니다.";
    private static final String WRONG_COORDINATE_X = "설정한 주소의 X좌표의 값이 잘못되었습니다.";
    private static final String WRONG_COORDINATE_Y = "설정한 주소의 Y좌표의 값이 잘못되었습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "address")
    private Paper paper;

    // TODO : 이거 패턴으로 하던가, 아니면 정규식 메서드를 쓰던가
    @Pattern(
            regexp = "^([가-힣]+(도|특별시|광역시|시|군|구)\\s)+([가-힣0-9]+(읍|면|동|리|로|길)\\s)+\\d+(-\\d+)*$",
            message = "잘못된 주소 형식입니다. 주소를 확인해 주세요."
            )
    @Column(name = "city", nullable = false, columnDefinition = "varchar(70)")
    private String city;

    @Column(name = "store", nullable = false, columnDefinition = "varchar(50)")
    private String store;

    @Column(name = "coordinate_x", nullable = false, columnDefinition = "decimal(17,15)")
    private Double coordinateX;

    @Column(name = "coordinate_y", nullable = false, columnDefinition = "decimal(18,15)")
    private Double coordinateY;

    /**
     * Address 객체 생성자
     *
     * @param city        도로명 주소
     * @param store       상호명
     * @param coordinateX 위도
     * @param coordinateY 경도
     */
    @Builder(builderMethodName = "createBuilder")
    public Address(String city, String store, double coordinateX, double coordinateY) {
        this.city = validateAddressSize(city, 30, "주소");
        this.store = validateAddressSize(store, 20, "상호명");
        this.coordinateX = validateCoordinateX(coordinateX);
        this.coordinateY = validateCoordinateY(coordinateY);
    }

    /**
     * Address 객체 업데이트
     *
     * @param city        도로명 주소
     * @param store       상호명
     * @param coordinateX 위도
     * @param coordinateY 경도
     * @return 업데이트 된 객체
     */
    public Address update(String city, String store, double coordinateX, double coordinateY) {
        this.city = validateAddressSize(city, 30, "주소");
        this.store = validateAddressSize(store, 20, "상호명");
        this.coordinateX = validateCoordinateX(coordinateX);
        this.coordinateY = validateCoordinateY(coordinateY);

        return this;
    }

    /**
     * Paper 객체를 지정한다.
     *
     * @param paper Paper 객체
     */
    public void setPaper(Paper paper) {
        this.paper = paper;

        if (paper.getAddress() != this) {
            paper.setAddress(this);
        }
    }

    private String validateAddressSize(String validateTarget, int size, String fieldName) {
        if (validateTarget.length() > size) {
            throw new ValidationException(String.format(PARAMETER_TOO_LONG, fieldName), ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        return validateTarget;
    }

    private Double validateCoordinateX(Double coordinateX) {
        if (coordinateX > 100 || coordinateX < -100) {
            throw new ValidationException(WRONG_COORDINATE_X, ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        validateCoordinate(coordinateX);

        return coordinateX;
    }

    private Double validateCoordinateY(Double coordinateY) {
        if (coordinateY > 1000 || coordinateY < -1000) {
            throw new ValidationException(WRONG_COORDINATE_Y, ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
        }

        validateCoordinate(coordinateY);

        return coordinateY;
    }

    private void validateCoordinate(Double coordinate) {
        String coordinateStr = coordinate.toString();
        int indexOfDecimal = coordinateStr.indexOf('.');
        if (indexOfDecimal != -1) {
            int decimalPlaces = coordinateStr.length() - indexOfDecimal - 1;
            if (decimalPlaces > 15) {
                throw new ValidationException("소수점 자릿수가 15자리를 초과합니다: ", ErrorCode.VALIDATION_PARAMETER_EXCEPTION);
            }
        }
    }

}
