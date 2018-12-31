package com.dmytroverner.mobileappws.service;

import com.dmytroverner.mobileappws.dto.AddressDto;
import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.entity.AddressEntity;
import com.dmytroverner.mobileappws.entity.UserEntity;
import com.dmytroverner.mobileappws.exceptions.UserServiceException;
import com.dmytroverner.mobileappws.repository.UserRepository;
import com.dmytroverner.mobileappws.service.impl.UserServiceImpl;
import com.dmytroverner.mobileappws.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    Utils utils;

    private final String FIRST_NAME = "Jane";
    private final String EMAIL = "test@test.com";
    private final String ADDRESS_ID = "tr32d2jh32";
    private final String USER_ID = "hhtt44";
    private final String ENCRYPTED_PASSWORD = "14yth574kkfd";
    private final String PASSWORD = "123qwe";

    private  UserEntity storedUserEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        storedUserEntity = new UserEntity();
        storedUserEntity.setId(1L);
        storedUserEntity.setFirstName(FIRST_NAME);
        storedUserEntity.setUserId(USER_ID);
        storedUserEntity.setEncryptedPassword(ENCRYPTED_PASSWORD);
        storedUserEntity.setAddresses(getAddressesEntity());
    }

    @Test
    public void getUserReturnsUserByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(storedUserEntity);

        UserDto userDto = userService.getUser(EMAIL);

        assertNotNull(userDto);
        assertEquals(FIRST_NAME, userDto.getFirstName());
    }

    @Test
    public void throwsUserNotFoundExceptionWhenNoUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(EMAIL));
    }

    @Test
    public void createUserReturnsCreatedUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateUUID()).thenReturn(ADDRESS_ID);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(any(UserEntity.class))).thenReturn(storedUserEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setPassword(PASSWORD);

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(storedUserEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(storedUserEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserEntity.getAddresses().size(), storedUserDetails.getAddresses().size());
        verify(utils, atLeast(2)).generateUUID();
        verify(bCryptPasswordEncoder).encode(PASSWORD);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void attemptToCreateUserWithExistingEmailThrowsUserServiceException() {
        when(userRepository.findByEmail(anyString())).thenReturn(storedUserEntity);
        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);

        assertThrows(UserServiceException.class, () -> userService.createUser(userDto));
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("Chicago");
        addressDto.setCountry("USA");

        AddressDto billingAddressDto = new AddressDto();
        addressDto.setType("billing");
        addressDto.setCity("Toronto");
        addressDto.setCountry("Canada");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);

        return addresses;
    }

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressesDto();

        Type listType = new TypeToken<List<AddressEntity>>(){}.getType();

        return new ModelMapper().map(addresses, listType);
    }
}
