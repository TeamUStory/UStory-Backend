package com.elice.ustory.domain.address;

import com.elice.ustory.domain.recommand.dto.RecommendCountDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.elice.ustory.domain.address.QAddress.address;

@Repository
@RequiredArgsConstructor
public class AddressQueryDslRepositoryImpl implements AddressQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RecommendCountDTO> countEqualAddress() {

        return jpaQueryFactory
                .select(Projections.constructor(RecommendCountDTO.class,
                        address.store,
                        address.city,
                        address.coordinateX,
                        address.coordinateY,
                        address.id.count()
                ))
                .from(address)
                .groupBy(address.store, address.city, address.coordinateX, address.coordinateY)
                .orderBy(address.id.count().desc())
                .fetch();
    }

    @Override
    public List<RecommendCountDTO> countEqualAddress(Pageable pageable, LocalDateTime requestTime) {

        return jpaQueryFactory
                .select(Projections.constructor(RecommendCountDTO.class,
                        address.store,
                        address.city,
                        address.coordinateX,
                        address.coordinateY,
                        address.id.count()
                ))
                .from(address)
                .where(address.createdAt.loe(requestTime))
                .groupBy(address.store, address.city, address.coordinateX, address.coordinateY)
                .orderBy(address.id.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}
