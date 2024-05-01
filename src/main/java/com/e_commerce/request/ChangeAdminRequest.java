package com.e_commerce.request;

import lombok.Getter;

@Getter
public class ChangeAdminRequest {

    private Long adminId;
    private Long storeId;
    private Long oldAdminId;
}
