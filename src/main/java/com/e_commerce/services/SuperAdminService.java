package com.e_commerce.services;

import com.e_commerce.Dto.AdminDto;
import com.e_commerce.Dto.StoreDto;
import com.e_commerce.request.ChangeAdminRequest;
import com.e_commerce.response.ApiResponse;

public interface SuperAdminService {
    ApiResponse<AdminDto> createAdmin(AdminDto adminDto);

    ApiResponse<StoreDto> addStore(StoreDto storeDto);

    ApiResponse<?> deActivateStore(Long storeId);

    ApiResponse<?> reActivateStore(Long storeId);

    ApiResponse<?> getAllInactiveStores();

    ApiResponse<?> getAllInactiveAdmins();

    ApiResponse<?> changeAdmin(ChangeAdminRequest changeAdminRequest);

    ApiResponse<?> getAllActiveStores();

    ApiResponse<?> getAllActiveAdmins();

    ApiResponse<?> getAllActiveStoresCount();

    ApiResponse<?> getAllActiveAdminsCount();

    ApiResponse<?> getAllStoresCount();

    ApiResponse<?> getAllAdminsCount();
}
