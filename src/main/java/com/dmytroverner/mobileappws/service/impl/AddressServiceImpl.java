package com.dmytroverner.mobileappws.service.impl;

import com.dmytroverner.mobileappws.dto.AddressDto;
import com.dmytroverner.mobileappws.entity.AddressEntity;
import com.dmytroverner.mobileappws.entity.UserEntity;
import com.dmytroverner.mobileappws.repository.AddressRepository;
import com.dmytroverner.mobileappws.repository.UserRepository;
import com.dmytroverner.mobileappws.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private ModelMapper modelMapper;

    @Autowired
    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository) {
        modelMapper = new ModelMapper();
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> result = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            return result;

        List<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addresses) {
            result.add(modelMapper.map(addressEntity, AddressDto.class));
        }

        return result;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if (addressEntity != null) {
            return modelMapper.map(addressEntity, AddressDto.class);
        }
        return null;
    }
}
