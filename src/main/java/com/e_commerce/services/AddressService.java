package com.e_commerce.services;

import com.e_commerce.Dto.AddressDto;
import com.e_commerce.response.ApiResponse;

import java.util.List;

public interface AddressService {

    ApiResponse<AddressDto> addAddress(AddressDto addressDto);

    ApiResponse<List<AddressDto>> getUserAddress(Integer userId);

    ApiResponse<AddressDto> getAddressById(Long addressId);

    ApiResponse<AddressDto> updateAddress(AddressDto addressDto);

    ApiResponse<String> deleteAddress(Long addressId);
}
