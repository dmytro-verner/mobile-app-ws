package com.dmytroverner.mobileappws.service;

import com.dmytroverner.mobileappws.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);
}
