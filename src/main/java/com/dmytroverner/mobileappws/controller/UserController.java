package com.dmytroverner.mobileappws.controller;

import com.dmytroverner.mobileappws.dto.AddressDto;
import com.dmytroverner.mobileappws.dto.UserDto;
import com.dmytroverner.mobileappws.model.request.UserDetailsRequestModel;
import com.dmytroverner.mobileappws.model.response.AddressResponse;
import com.dmytroverner.mobileappws.model.response.OperationStatusModel;
import com.dmytroverner.mobileappws.model.response.RequestOperationStatus;
import com.dmytroverner.mobileappws.model.response.UserDetailsResponse;
import com.dmytroverner.mobileappws.service.AddressService;
import com.dmytroverner.mobileappws.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    private ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;

        modelMapper = new ModelMapper();
    }

    @GetMapping(path="/{userId}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        return modelMapper.map(userDto, UserDetailsResponse.class);
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse createUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        UserDto userDto = modelMapper.map(userDetailsRequestModel, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        return modelMapper.map(createdUser, UserDetailsResponse.class);
    }

    @PutMapping(path="/{userId}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserDetailsResponse putUser(@PathVariable("userId") String userId, @RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        UserDto userDto = modelMapper.map(userDetailsRequestModel, UserDto.class);

        UserDto updatedUser = userService.updateUser(userId, userDto);

        return modelMapper.map(updatedUser, UserDetailsResponse.class);
    }

    @DeleteMapping(path = "/{userId}",
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OperationStatusModel deleteUser(@PathVariable("userId") String userId) {
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
            UserDetailsResponse userDetailsResponse = modelMapper.map(userDto, UserDetailsResponse.class);
            returnList.add(userDetailsResponse);
        }

        return returnList;
    }

    @GetMapping(path="/{userId}/addresses",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<AddressResponse> getUserAddresses(@PathVariable("userId") String userId) {
        List<AddressResponse> result = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(userId);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressResponse>>() {}.getType();
            result = modelMapper.map(addressesDto, listType);
        }
        return result;
    }

    @GetMapping(path="/{userId}/addresses/{addressId}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public AddressResponse getUserAddress(@PathVariable("addressId") String addressId) {
        AddressDto address = addressService.getAddress(addressId);
        if (address != null) {
            return modelMapper.map(address, AddressResponse.class);
        }
        return null;
    }
}
