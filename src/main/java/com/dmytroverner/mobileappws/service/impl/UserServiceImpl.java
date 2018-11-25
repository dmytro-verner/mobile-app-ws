package com.dmytroverner.mobileappws.service.impl;

import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.entity.UserEntity;
import com.dmytroverner.mobileappws.repository.UserRepository;
import com.dmytroverner.mobileappws.service.UserService;
import com.dmytroverner.mobileappws.shared.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        if (userEntity != null)
            throw new RuntimeException("record already exists.");
        userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto, userEntity);

        userEntity.setEncryptedPassword("test");
        userEntity.setUserId(utils.generateUUID());

        UserEntity storedUserDetails = userRepository.save(userEntity);
        UserDto returnUserDetails = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnUserDetails);

        return returnUserDetails;
    }
}
