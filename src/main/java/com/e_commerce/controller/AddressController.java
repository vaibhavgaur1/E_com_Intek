package com.e_commerce.controller;

import com.e_commerce.Dto.AddressDto;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.AddressService;
import com.e_commerce.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/address")
@Slf4j
public class AddressController {
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;

    @PostMapping("/addAddress")
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(@RequestBody AddressDto addressDto) {
//        String message= userService.addAddress(addressDto)? "Address Added": "Something Went Wrong";
        return ResponseEntity.ok(addressService.addAddress(addressDto));
    }

    @GetMapping("/getAddressByUser/{userId}")
    public  ResponseEntity<ApiResponse<List<AddressDto>>>  getUserAddresses(@PathVariable Integer userId ) {
//        String message= userService.getUserAddress(addressDto)? "Address Added": "Something Went Wrong";
        return ResponseEntity.ok(addressService.getUserAddress(userId));
    }

    @GetMapping("/getAddressByAddress/{addressId}")
    public  ResponseEntity<ApiResponse<AddressDto>>  getAddressById(@PathVariable Long addressId ) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    @PatchMapping("/updateAddress")
    public  ResponseEntity<ApiResponse<AddressDto>>  updateAddress(@RequestBody AddressDto addressDto ) {
        return ResponseEntity.ok(addressService.updateAddress(addressDto));
    }

    @DeleteMapping("/deleteAddress")
    public  ResponseEntity<ApiResponse<String>>  deleteAddress(@PathVariable Long addressId ) {
        return ResponseEntity.ok(addressService.deleteAddress(addressId));
    }
}
