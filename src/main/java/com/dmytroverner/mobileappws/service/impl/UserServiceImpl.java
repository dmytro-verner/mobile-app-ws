package com.dmytroverner.mobileappws.service.impl;

import com.dmytroverner.mobileappws.dto.AddressDto;
import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.entity.UserEntity;
import com.dmytroverner.mobileappws.exceptions.UserServiceException;
import com.dmytroverner.mobileappws.model.response.ErrorMessages;
import com.dmytroverner.mobileappws.repository.UserRepository;
import com.dmytroverner.mobileappws.service.UserService;
import com.dmytroverner.mobileappws.shared.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private Utils utils;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        if (userEntity != null)
            throw new UserServiceException("Record already exists.");

        IntStream.range(0, userDto.getAddresses().size()).forEach(i -> {
            AddressDto address = userDto.getAddresses().get(i);
            address.setUserDetails(userDto);
            address.setAddressId(utils.generateUUID());
            userDto.getAddresses().set(i, address);
        });

        ModelMapper modelMapper = new ModelMapper();
        userEntity = modelMapper.map(userDto, UserEntity.class);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(utils.generateUUID());

        UserEntity storedUserDetails = userRepository.save(userEntity);

        return modelMapper.map(storedUserDetails, UserDto.class);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto userDto = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserEntity, returnValue);
        return returnValue;
    }

    @Override
    public void delete(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnList = new ArrayList<>();

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnList.add(userDto);
        }
        return returnList;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
