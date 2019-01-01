package com.dmytroverner.mobileappws.controller;

import com.dmytroverner.mobileappws.dto.AddressDto;
import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.model.response.UserDetailsResponse;
import com.dmytroverner.mobileappws.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImpl userService;

    private final String FIRST_NAME = "Kyle";
    private final String LAST_NAME = "Reese";
    private final String EMAIL = "test@test.com";
    private final String USER_ID = "hhtt44";

    UserDto userDto;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userDto = new UserDto();
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setUserId(USER_ID);
        userDto.setEmail(EMAIL);
        userDto.setAddresses(getAddressesDto());
    }

    @Test
    public void getUserReturnsUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserDetailsResponse user = userController.getUser(USER_ID);

        assertNotNull(user);
        assertEquals(USER_ID, user.getUserId());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(userDto.getAddresses().size(), user.getAddresses().size());
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
}
