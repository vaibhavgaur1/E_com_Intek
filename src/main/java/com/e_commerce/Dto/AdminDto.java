package com.e_commerce.Dto;

import com.e_commerce.entity.Admin;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class AdminDto {

    private Long id;
    private String serviceNo;
    private String name;
    private String email;
    private String password;
    private String contact;
    private boolean isActive;

    private String role;

    public Admin generateEntity() {
        return Admin.builder()
                .id(id)
                .serviceNo(serviceNo)
                .name(name)
                .email(email)
                .password(password)
                .contact(contact)
                .isActive(isActive)
                .build();
    }
}
