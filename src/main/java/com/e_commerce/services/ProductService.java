package com.e_commerce.services;

import com.e_commerce.entity.Product;
import com.e_commerce.request.AddProductRequest;
import com.e_commerce.request.UpdateProductRequest;
import com.e_commerce.response.AddProductResponse;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ApiResponse<Product> saveProduct(Product product);

    ApiResponse<List<ProductResponse>> getAllProducts(String authHeader, String cardType, String searchKey) throws Exception;    //Integer pageNumber, Integer pageSize, String searchKey

    void deleteProductDetails(Integer productId);

    ApiResponse<Product> getProductById(Integer productId);

    ApiResponse<List<Product>> getProductDetails(Boolean isSingleProductCheckout, Integer productId, String authHeader) throws Exception;

    ApiResponse<AddProductResponse> addProduct(AddProductRequest req);

    ApiResponse<AddProductResponse> updateProduct(UpdateProductRequest req) throws Exception;


    ApiResponse<List<ProductResponse>> getAllProducts(String searchKey);
}
