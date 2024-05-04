package com.e_commerce.controller;

import com.e_commerce.Dto.AdminDto;
import com.e_commerce.Dto.MessageInput;
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

    @GetMapping("/active-stores-count")
    public ResponseEntity<ApiResponse<?>> getAllActiveStoresCount(){
        return ResponseEntity.ok(superAdminService.getAllActiveStoresCount());
    }
    @GetMapping("/stores-count")
    public ResponseEntity<ApiResponse<?>> getAllStoresCount(){
        return ResponseEntity.ok(superAdminService.getAllStoresCount());
    }

    @GetMapping("/inactive-admins")
    public ResponseEntity<ApiResponse<?>> getAllInactiveAdmins(){
        return ResponseEntity.ok(superAdminService.getAllInactiveAdmins());
    }

    @GetMapping("/active-admins")
    public ResponseEntity<ApiResponse<?>> getAllActiveAdmins(){
        return ResponseEntity.ok(superAdminService.getAllActiveAdmins());
    }

    @GetMapping("/active-admins-count")
    public ResponseEntity<ApiResponse<?>> getAllActiveAdminsCount(){
        return ResponseEntity.ok(superAdminService.getAllActiveAdminsCount());
    }

    @GetMapping("/admins-count")
    public ResponseEntity<ApiResponse<?>> getAllAdminsCount(){
        return ResponseEntity.ok(superAdminService.getAllAdminsCount());
    }

    @GetMapping("/inactivate-store/{storeId}")
    public ResponseEntity<ApiResponse<?>> deactivateStore(@PathVariable Long storeId){
        return ResponseEntity.ok(superAdminService.deActivateStore(storeId));
    }
    @GetMapping("/re-activate-store/{storeId}")
    public ResponseEntity<ApiResponse<?>> reactivateStore(@PathVariable Long storeId){
        return ResponseEntity.ok(superAdminService.reActivateStore(storeId));
    }

    @PostMapping("/change-admin")
    public ResponseEntity<ApiResponse<?>> changeAdmin(@RequestBody ChangeAdminRequest changeAdminRequest){
        return ResponseEntity.ok(superAdminService.changeAdmin(changeAdminRequest));
    }

    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<?>> getAllAdmins(){
        return ResponseEntity.ok(superAdminService.getAllAdmins());
    }
    @GetMapping("/stores")
    public ResponseEntity<ApiResponse<?>> getAllStores(){
        return ResponseEntity.ok(superAdminService.getAllStores());
    }

    @GetMapping("/activate-admin/{adminId}")
    public ResponseEntity<ApiResponse<?>> activateAdmin(@PathVariable Long adminId){
        return ResponseEntity.ok(superAdminService.activateAdmin(adminId));
    }

    @GetMapping("/deactivate-admin/{adminId}")
    public ResponseEntity<ApiResponse<?>> deactivateAdmin(@PathVariable Long adminId){
        return ResponseEntity.ok(superAdminService.deactivateAdmin(adminId));
    }

    @PostMapping("/publish-message")
    public ResponseEntity<ApiResponse<?>> publishMessage(@RequestBody MessageInput message){
        return ResponseEntity.ok(superAdminService.publishMessage(message));
    }



}
