package com.e_commerce.services.impl;

import com.e_commerce.Dto.AddressDto;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.dao.AddressDao;
import com.e_commerce.dao.UserDao;
import com.e_commerce.entity.Address;
import com.e_commerce.entity.User;
import com.e_commerce.exception.RecordNotFoundException;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.AddressService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private  final AddressDao addressDao;
    private final UserDao userDao;
    @SneakyThrows
    public ApiResponse<AddressDto> addAddress(AddressDto addressDto) {
        User dbUser = userDao.findById(addressDto.getUserId())
                .orElseThrow(() -> new Exception("User Not Found"));
        Address address= Address.builder()
                .flatNumberOrHouseNumber(addressDto.getFlatNumberOrHouseNumber())
                .pinCode(addressDto.getPinCode())
                .area(addressDto.getArea())
                .landMark(addressDto.getLandMark())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .addressType(addressDto.getAddressType())
                .user(dbUser)
                .build();

        Address save = addressDao.save(address);
//        AddressDto addressDto1 = new AddressDto();
        AddressDto addressDto1 = AddressDto.builder()
                .flatNumberOrHouseNumber(save.getFlatNumberOrHouseNumber())
                .pinCode(addressDto.getPinCode())
                .id(save.getId())
                .userId(save.getUser().getId())
                .area(save.getArea())
                .landMark(save.getLandMark())
                .city(save.getCity())
                .state(save.getState())
                .addressType(save.getAddressType())
                .build();
        return ResponseUtils.createSuccessResponse(addressDto1, new TypeReference<AddressDto>() {});


    }

    public ApiResponse<List<AddressDto>> getUserAddress(Integer userId) {
        if (userDao.existsById(userId)){
            List<AddressDto> addressDtos = addressDao.findByUserId(userId)
                    .stream()
                    .map(Address::generateDto)
                    .toList();
            return ResponseUtils.createSuccessResponse(addressDtos, new TypeReference<List<AddressDto>>() {});
        }else{
            return ResponseUtils.createSuccessResponse(Collections.emptyList(), new TypeReference<List<AddressDto>>() {});
        }
    }

    @Override
    public ApiResponse<AddressDto> getAddressById(Long addressId) {

        Address dbAddress = addressDao.findById(addressId)
                .orElseThrow(() -> new RecordNotFoundException("address not found"));

        return ResponseUtils.createSuccessResponse(dbAddress.generateDto(), new TypeReference<>() {});
    }

    @Override
    public ApiResponse<AddressDto> updateAddress(AddressDto addressDto) {

        Address dbAddress = addressDao.findById(addressDto.getId())
                .orElseThrow(() -> new RecordNotFoundException("address not found"));


        dbAddress.setFlatNumberOrHouseNumber(addressDto.getFlatNumberOrHouseNumber());
        dbAddress.setArea(addressDto.getArea());
        dbAddress.setLandMark(addressDto.getLandMark());
        dbAddress.setCity(addressDto.getCity());
        dbAddress.setState(addressDto.getState());
        dbAddress.setAddressType(addressDto.getAddressType());

        AddressDto updated = addressDao.save(dbAddress).generateDto();

        return ResponseUtils.createSuccessResponse(updated, new TypeReference<>() {});
    }

    @SneakyThrows
    @Override
    public ApiResponse<String> deleteAddress(Long addressId) {

        if(addressId == null || addressId < 0){
            return ResponseUtils.createFailure("invalid address id", HttpStatus.BAD_REQUEST.value());
        }
        if(addressDao.existsById(addressId)){
            addressDao.deleteById(addressId);
            return ResponseUtils.createSuccessResponse("address deleted", new TypeReference<>() {});
        }else{
            return null;
        }

    }
}
