package com.elice.ustory.domain.address;

import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private static final String NOT_FOUND_ADDRESS_MESSAGE = "%d: 해당하는 주소가 존재하지 않습니다.";

    private final AddressRepository addressRepository;

    public Address create(Address address, Paper paper) {
        address.setPaper(paper);
        return addressRepository.save(address);
    }

    public Address update(Long addressId, Address address) {

        Address savedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ADDRESS_MESSAGE, addressId)));

        savedAddress.update(
                address.getCity(),
                address.getStore(),
                address.getCoordinateX(),
                address.getCoordinateY()
        );

        return savedAddress;
    }

}
