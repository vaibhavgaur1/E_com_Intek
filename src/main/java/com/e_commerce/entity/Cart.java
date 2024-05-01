package com.e_commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    private Integer cartId;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Product product;

    private Long productQuantity;

    @ManyToOne(cascade = CascadeType.DETACH)
    private User user;

}
