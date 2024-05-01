package com.e_commerce.Dto;

import com.e_commerce.entity.Address;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class UserDto {

    private int id;
    private String name;
    private String pan;
    private String adhaar;
    private String contactNumber;
    private String liquorCardNumber;
    private String groceryCardNumber;
    private String dob;
    private Long otp;
    private boolean isActive;

}
