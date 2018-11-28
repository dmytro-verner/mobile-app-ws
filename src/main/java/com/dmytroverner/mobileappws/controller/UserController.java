package com.dmytroverner.mobileappws.controller;

import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.model.request.UserDetailsRequest;
import com.dmytroverner.mobileappws.model.response.OperationStatusModel;
import com.dmytroverner.mobileappws.model.response.RequestOperationStatus;
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
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
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

    @PutMapping(path="/{id}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse putUser(@PathVariable("id") String userId, @RequestBody UserDetailsRequest userDetailsRequest) {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequest, userDto);

        UserDto updatedUser = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(updatedUser, userDetailsResponse);

        return userDetailsResponse;
    }

    @DeleteMapping(path = "/{id}",
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OperationStatusModel deleteUser(@PathVariable("id") String userId) {
        OperationStatusModel operationStatusModel = new OperationStatusModel();
        operationStatusModel.setOperationName(RequestOperationName.DELETE.name());

        userService.delete(userId);

        operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return operationStatusModel;
    }
}
