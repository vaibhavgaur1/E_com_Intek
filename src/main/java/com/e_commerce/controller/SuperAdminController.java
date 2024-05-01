package com.e_commerce.controller;

import com.e_commerce.Dto.AdminDto;
import com.e_commerce.Dto.StoreDto;
import com.e_commerce.request.ChangeAdminRequest;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.SuperAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/super-admin")
@Slf4j
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<?>> createAdmin(@RequestBody AdminDto adminDto) {
        return ResponseEntity.ok(superAdminService.createAdmin(adminDto));
    }

    @PostMapping("/add-store")
    public ResponseEntity<ApiResponse<?>> addStore(@RequestBody StoreDto storeDto) {
        return ResponseEntity.ok(superAdminService.addStore(storeDto));
    }

    @GetMapping("/inactive-stores")
    public ResponseEntity<ApiResponse<?>> getAllInactiveStores(){
        return ResponseEntity.ok(superAdminService.getAllInactiveStores());
    }

    @GetMapping("/active-stores")
    public ResponseEntity<ApiResponse<?>> getAllActiveStores(){
        return ResponseEntity.ok(superAdminService.getAllActiveStores());
    }

    @GetMapping("/inactive-admins")
    public ResponseEntity<ApiResponse<?>> getAllInactiveAdmins(){
        return ResponseEntity.ok(superAdminService.getAllInactiveAdmins());
    }

    @GetMapping("/active-admins")
    public ResponseEntity<ApiResponse<?>> getAllActiveAdmins(){
        return ResponseEntity.ok(superAdminService.getAllActiveAdmins());
    }

    @GetMapping("/inactivate-store/{storeId}")
    public ResponseEntity<ApiResponse<?>> deactivateStore(@PathVariable Long storeId){
        return ResponseEntity.ok(superAdminService.deActivateStore(storeId));
    }
    @GetMapping("/re-activate-store/{storeId}")
    public ResponseEntity<ApiResponse<?>> reactivateStore(@PathVariable Long storeId){
        return ResponseEntity.ok(superAdminService.reActivateStore(storeId));
    }

    @GetMapping("/change-admin/{storeId}")
    public ResponseEntity<ApiResponse<?>> changeAdmin(@RequestBody ChangeAdminRequest changeAdminRequest){
        return ResponseEntity.ok(superAdminService.changeAdmin(changeAdminRequest));
    }

}
