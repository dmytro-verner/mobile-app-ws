package com.dmytroverner.mobileappws.service;

import com.dmytroverner.mobileappws.dto.AddressDto;

import java.util.List;

public interface AddressService {

    List<AddressDto> getAddresses(String userId);

    AddressDto getAddress(String addressId);
}
