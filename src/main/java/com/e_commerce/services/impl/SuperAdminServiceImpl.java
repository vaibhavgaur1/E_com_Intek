package com.e_commerce.services.impl;

import com.e_commerce.Dto.AdminDto;
import com.e_commerce.Dto.StoreDto;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.dao.AdminDao;
import com.e_commerce.dao.RoleDao;
import com.e_commerce.dao.StoreDao;
import com.e_commerce.entity.Admin;
import com.e_commerce.entity.Role;
import com.e_commerce.entity.Store;
import com.e_commerce.exception.InvalidInputException;
import com.e_commerce.exception.RecordNotFoundException;
import com.e_commerce.request.ChangeAdminRequest;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.SuperAdminService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {
    private final AdminDao adminDao;
    private final StoreDao storeDao;
    private final PasswordEncoder passwordEncoder;
    private  final RoleDao roleDao;

    @SneakyThrows
    @Override
    public ApiResponse<AdminDto> createAdmin(AdminDto adminDto) {

        if(adminDto.getEmail() == null || adminDto.getEmail().isBlank() || adminDto.getEmail().isEmpty()){
            throw  new InvalidInputException("email can not be empty");
        }
        if(adminDto.getPassword() == null || adminDto.getPassword().isBlank() || adminDto.getPassword().isEmpty()){
            throw  new InvalidInputException("password can not be empty or blank");
        }
        if(adminDto.getContact() == null){
            throw  new InvalidInputException("contact number is mandatory");
        }

        if(String.valueOf(adminDto.getContact()).length()!= 10){
            throw  new InvalidInputException("contact number should have 10 digits");
        }

        if(adminDao.existsByEmail(adminDto.getEmail()))
            throw new Exception("email already present");

        Role role = roleDao.findById("Admin").orElseThrow(() -> new RecordNotFoundException("role not found"));


        Admin admin = Admin.builder()
                .serviceNo(adminDto.getServiceNo())
                .name(adminDto.getName())
                .email(adminDto.getEmail())
                .password(passwordEncoder.encode(adminDto.getPassword()))
                .contact(adminDto.getContact())
                .isActive(false)
                .roles(Set.of(role))
                .build();

        AdminDto savedAdmin = adminDao.save(admin).generateDto();


        return ResponseUtils.createSuccessResponse(savedAdmin, new TypeReference<AdminDto>() {});
    }

    @Override
    public ApiResponse<StoreDto> addStore(StoreDto storeDto) {

        Admin dbAdmin = adminDao.findById(storeDto.getId())
                .orElseThrow(() -> new RecordNotFoundException("admin not found"));


        Store store = Store.builder()
                .id(storeDto.getId())
                .name(storeDto.getName())
                .contact(storeDto.getContact())
                .address(storeDto.getAddress())
                .isActive(true)
                .admin(dbAdmin)
                .build();

        StoreDto savedStore = storeDao.save(store).generateDto();
        adminDao.activateAdmin(dbAdmin.getId(), true);

        return ResponseUtils.createSuccessResponse(savedStore, new TypeReference<StoreDto>() {});
    }

    @Override
    public ApiResponse<?> deActivateStore(Long storeId) {
        if(!storeDao.existsById(storeId)){
            throw new RecordNotFoundException("store not found");
        }

        int i = storeDao.disableStore(false, storeId);
        if(i!= 1)
            return ResponseUtils.createSuccessResponse("store is disabled", new TypeReference<String>() {});
        else
            return ResponseUtils.createFailure("something went wrong", 500);
    }

    @Override
    public ApiResponse<?> reActivateStore(Long storeId) {
        if(!storeDao.existsById(storeId)){
            throw new RecordNotFoundException("store not found");
        }

        int i = storeDao.enableStore(true, storeId);
        if(i!= 1)
            return ResponseUtils.createSuccessResponse("store is enabled", new TypeReference<String>() {});
        else
            return ResponseUtils.createFailure("something went wrong", 500);
    }

    @Override
    public ApiResponse<?> getAllInactiveStores() {
        List<StoreDto> list = storeDao.findByIsActive(false).stream().map(Store::generateDto).toList();
        return ResponseUtils.createSuccessResponse(list, new TypeReference<List<StoreDto>>() {});
    }

    @Override
    public ApiResponse<?> getAllInactiveAdmins() {
        List<AdminDto> list = adminDao.findByIsActive(false).stream().map(Admin::generateDto).toList();
        return ResponseUtils.createSuccessResponse(list, new TypeReference<List<AdminDto>>() {});
    }

    @Override
    public ApiResponse<?> changeAdmin(ChangeAdminRequest changeAdminRequest) {

        try{



            Store dbStore = storeDao.findById(changeAdminRequest.getStoreId())
                    .orElseThrow(() -> new RecordNotFoundException("store not found"));

            Admin dbOldAdmin = dbStore.getAdmin();
            if(!dbOldAdmin.isActive()){
                throw new Exception("old admin is already inactive");
            }

            dbOldAdmin.setActive(false);
            adminDao.save(dbOldAdmin);

            Admin dbAdmin = adminDao.findById(changeAdminRequest.getAdminId())
                    .orElseThrow(() -> new RecordNotFoundException("new admin not found in records"));

            dbStore.setAdmin(dbAdmin);
            storeDao.save(dbStore);

            return ResponseUtils.createSuccessResponse("admin changed to :"+dbAdmin.getName(), new TypeReference<String>() {});

        }catch (Exception e){
            return ResponseUtils.createFailure("something went wrong",  500);
        }
    }

    @Override
    public ApiResponse<?> getAllActiveStores() {
        List<StoreDto> list = storeDao.findByIsActive(true).stream().map(Store::generateDto).toList();
        return ResponseUtils.createSuccessResponse(list, new TypeReference<List<StoreDto>>() {});
    }

    @Override
    public ApiResponse<?> getAllActiveAdmins() {
        List<AdminDto> list = adminDao.findByIsActive(true).stream().map(Admin::generateDto).toList();
        return ResponseUtils.createSuccessResponse(list, new TypeReference<List<AdminDto>>() {});
    }
}
