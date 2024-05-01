package com.e_commerce.entity;

import com.e_commerce.Dto.StoreDto;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity @Table @Builder @AllArgsConstructor @NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Long contact;
    private String address;

    private boolean isActive;

    @OneToOne(fetch = FetchType.EAGER)
    private Admin admin;

    public StoreDto generateDto(){
        return StoreDto.builder()
                .id(id)
                .name(name)
                .contact(contact)
                .address(address)
                .isActive(isActive)
                .adminId(admin.getId())
                .build();
    }
}
