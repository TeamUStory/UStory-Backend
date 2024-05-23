package com.elice.ustory.domain.page.repository;

import com.elice.ustory.domain.page.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
