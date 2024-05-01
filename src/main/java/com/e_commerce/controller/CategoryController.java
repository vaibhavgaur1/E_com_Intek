package com.e_commerce.controller;

import com.e_commerce.Dto.CategoryDto;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-category")
    public ResponseEntity<ApiResponse<CategoryDto>> addCategory(@RequestBody CategoryDto categoryDto){
        return ResponseEntity.ok(categoryService.addCategory(categoryDto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/update-category")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@RequestBody CategoryDto categoryDto){
        return ResponseEntity.ok(categoryService.updateCategory(categoryDto));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/categories-name")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategoriesNamesOnly(){
        return ResponseEntity.ok(categoryService.getAllCategoriesNamesOnly());
    }
}
