package com.e_commerce.request;

import com.e_commerce.entity.Category;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class UpdateProductRequest {

    private Integer productId;
    private String productName;
    private String productDescription;
    private Double productDiscountedPrice;
    private Double productActualPrice;
    private String imageUrl;
    private String uploadId;
    private Long categoryId;
}
