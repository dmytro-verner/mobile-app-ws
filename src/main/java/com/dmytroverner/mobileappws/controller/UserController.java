package com.dmytroverner.mobileappws.controller;

import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.entity.UserEntity;
import com.dmytroverner.mobileappws.model.request.UserDetailsRequest;
import com.dmytroverner.mobileappws.model.response.UserDetailsResponse;
import com.dmytroverner.mobileappws.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path="/{id}",
        produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse getUser(@PathVariable("id") String userId) {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        UserDto userDto = userService.getUserByUserId(userId);

        BeanUtils.copyProperties(userDto, userDetailsResponse);
        return userDetailsResponse;
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse createUser(@RequestBody UserDetailsRequest userDetailsRequest) {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequest, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, userDetailsResponse);

        return userDetailsResponse;
    }

    @PutMapping
    public String putUser() {
        return "put was called";
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete was called";
    }
}
