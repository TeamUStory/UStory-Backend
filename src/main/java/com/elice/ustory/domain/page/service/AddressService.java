package com.elice.ustory.domain.page.service;

import com.elice.ustory.domain.page.entity.Address;
import com.elice.ustory.domain.page.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long addressId, Address address) {
        // TODO: 예외 처리 해야함
        Address savedAddress = addressRepository.findById(addressId).orElse(null);

        savedAddress.update(
                address.getCity(),
                address.getDetail(),
                address.getStore(),
                address.getCoordinateX(),
                address.getCoordinateY()
        );

        savedAddress.getPage().setAddress(savedAddress);

        return savedAddress;
    }

    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElse(null);
    }
}
