package com.dmytroverner.mobileappws.controller;

import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.model.request.UserDetailsRequestModel;
import com.dmytroverner.mobileappws.model.response.OperationStatusModel;
import com.dmytroverner.mobileappws.model.response.RequestOperationStatus;
import com.dmytroverner.mobileappws.model.response.UserDetailsResponse;
import com.dmytroverner.mobileappws.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public UserDetailsResponse createUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetailsRequestModel, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        return modelMapper.map(createdUser, UserDetailsResponse.class);
    }

    @PutMapping(path="/{id}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse putUser(@PathVariable("id") String userId, @RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

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

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<UserDetailsResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserDetailsResponse> returnList = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);
        for (UserDto userDto : users) {
            UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
            BeanUtils.copyProperties(userDto, userDetailsResponse);
            returnList.add(userDetailsResponse);
        }

        return returnList;
    }
}
