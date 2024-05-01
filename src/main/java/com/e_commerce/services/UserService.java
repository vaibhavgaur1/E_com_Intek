package com.e_commerce.services;

import com.e_commerce.Dto.AddressDto;
import com.e_commerce.Dto.OtpDto;
import com.e_commerce.Dto.RegisterDto;
import com.e_commerce.Dto.UserDto;
import com.e_commerce.entity.User;
import com.e_commerce.response.ApiResponse;
import jakarta.mail.MessagingException;

import java.util.List;

public interface UserService {

    ApiResponse<User> registerNewUser(RegisterDto registerDto) throws Exception;

//    void initRoleAndUser();

    boolean verifyOtp(OtpDto otpDto);

    ApiResponse<String> generateOtp(String email) throws MessagingException;


    ApiResponse disableAccount(String authHeader) throws Exception;

    ApiResponse updateUser(UserDto userDto);

    ApiResponse enableUser(Integer userId) throws Exception;

}
