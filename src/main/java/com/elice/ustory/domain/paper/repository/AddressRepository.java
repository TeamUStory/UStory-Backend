package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.paper.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
