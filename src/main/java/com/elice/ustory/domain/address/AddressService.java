package com.elice.ustory.domain.address;

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

        Address savedAddress = addressRepository.findById(addressId).orElse(null);

        if (savedAddress == null) {
            throw new IllegalArgumentException();
        }

        savedAddress.update(
                address.getCity(),
                address.getStore(),
                address.getCoordinateX(),
                address.getCoordinateY()
        );

        savedAddress.getPaper().setAddress(savedAddress);

        return savedAddress;
    }

    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElse(null);
    }
}
