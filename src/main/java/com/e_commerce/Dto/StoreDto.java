package com.e_commerce.Dto;

import lombok.*;

@Data@Getter@Setter@Builder@AllArgsConstructor@NoArgsConstructor
public class StoreDto {

    private Long id;

    private String name;
    private Long contact;
    private String address;
    private boolean isActive;

    private Long adminId;
}
