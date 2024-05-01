package com.e_commerce.services.impl;

import com.e_commerce.Dto.FileUpload;
import com.e_commerce._util.HelperUtils;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.dao.*;
import com.e_commerce.entity.*;
import com.e_commerce.exception.RecordNotFoundException;
import com.e_commerce.request.AddProductRequest;
import com.e_commerce.request.UpdateProductRequest;
import com.e_commerce.response.AddProductResponse;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.response.ProductResponse;
import com.e_commerce.services.FetchImage;
import com.e_commerce.services.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final HelperUtils helperUtils;
    private final FetchImage fetchImage;
    private final CartDao cartDao;
    private final CategoryRepository categoryRepository;
    private final FileUploadRepository fileUploadRepository;
    private final WishlistDao wishlistDao;
    @Autowired
    private ProductDao product;
    @SneakyThrows
    public ApiResponse<Product> saveProduct(Product product) {

        return ResponseUtils.createSuccessResponse(productDao.save(product), new TypeReference<Product>() {});
    }

    public ApiResponse<List<ProductResponse>> getAllProducts(String authHeader,
                                                             String cardType, String searchKey) throws Exception
//    Integer pageNumber, Integer pageSize, String searchKey
    {
        List<ProductResponse> response= new ArrayList<ProductResponse>();
//        Pageable pageable= PageRequest.of(pageNumber, pageSize);
        User dbUser = helperUtils.getUserFromAuthToken(authHeader);
        List<Wishlist> dbWishList = wishlistDao.findByUser(dbUser);
        if(searchKey== null || searchKey.isEmpty() || searchKey.isBlank())
        {
            List<Product> dbProducts=(List<Product>) productDao.findAllByCategoryType(cardType);
            System.out.println(dbProducts.size());

            dbProducts.forEach(dbProduct->{
                Optional<FileUpload> dbFileUploadForProduct  = fileUploadRepository.findById(dbProduct.getUploadId());
                boolean wishFlag=false;
                for (Wishlist wishlist : dbWishList){
                    if(wishlist.getProduct().getProductId()==dbProduct.getProductId()){
                        wishFlag=true;
                    }
                }
                ProductResponse product=new ProductResponse();
                product.setProductId(dbProduct.getProductId());
                product.setProductDescription(dbProduct.getProductDescription());
                product.setProductName(dbProduct.getProductName());
                product.setCategory(dbProduct.getCategory());
                product.setProductActualPrice(dbProduct.getProductActualPrice());
                product.setProductDiscountedPrice(dbProduct.getProductDiscountedPrice());
                product.setImageUrl(helperUtils.getCompleteImage() + dbFileUploadForProduct.get().getPathURL());
                product.setImage(helperUtils.getCompleteImage()+dbFileUploadForProduct.get().getPathURL());
                product.setWishList(wishFlag);
                product.setUploadId(dbProduct.getUploadId());
                product.setAvailableStock(dbProduct.getAvailableStock());
                response.add(product);
            });

            return ResponseUtils.createSuccessResponse(response, new TypeReference<List<ProductResponse>>() {});
        }
        else{
            List<Product> dbProducts = productDao
                    .findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase
                            (
                            searchKey , searchKey
                    );
            System.out.println(dbProducts.size());
            List<Product> withImage= new ArrayList<>();
            dbProducts.forEach(dbProduct->{
                FileUpload dbFileUploadForProduct = null;
                try {
                    dbFileUploadForProduct = fileUploadRepository.findById(dbProduct.getUploadId())
                            .orElseThrow(()->new Exception("no image url found"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

//                byte[] file = fetchImage.getFile(dbFileUploadForProduct.getPathURL());
//                dbProduct.setImage(file);

//                withImage.add(dbProduct);

            });



            return ResponseUtils.createSuccessResponse(response, new TypeReference<List<ProductResponse>>() {});
        }
    }

    public void deleteProductDetails(Integer productId) {
        productDao.deleteById(productId);
    }

    @SneakyThrows
    public ApiResponse<Product> getProductById(Integer productId) {
        Product dbProduct = productDao.findById(productId)
                .orElseThrow(() -> new Exception("Product Not Found"));
        FileUpload dbFileUploadForProduct = fileUploadRepository.findById(dbProduct.getUploadId())
                .orElseThrow(()->new Exception("no image url found"));

//        byte[] file = fetchImage.getFile(dbFileUploadForProduct.getPathURL());
//        dbProduct.setImage(file);

        dbProduct.setImageUrl(helperUtils.getPathForImage()+dbFileUploadForProduct.getPathURL());

        return ResponseUtils.createSuccessResponse(dbProduct, new TypeReference<Product>() {});
    }

    public ApiResponse<List<Product>> getProductDetails(Boolean isSingleProductCheckout, Integer productId, String authHeader) throws Exception {

        if(isSingleProductCheckout && productId!= 0){
            //we are going to buy a single product

            List<Product> productList= new ArrayList<>();
            Product product = productDao.findById(productId).orElseThrow(()-> new Exception("Product Not Found"));
            productList.add(product);
            return ResponseUtils.createSuccessResponse(productList, new TypeReference<List<Product>>() {});

        }else{
            //checkout entire cart
            User dbUser = helperUtils.getUserFromAuthToken(authHeader);
            List<Cart> dbListCart = cartDao.findByUser(dbUser);

            return ResponseUtils.createSuccessResponse(dbListCart.stream().map(Cart::getProduct).toList(), new TypeReference<List<Product>>() {});
        }
    }

    @SneakyThrows
    public ApiResponse<AddProductResponse> addProduct(AddProductRequest productDto) {
        AddProductResponse responce= new AddProductResponse();
        if(productDto.getCategoryId()== null){
            throw new Exception("please ensure valid category");
        }
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new Exception("category doesn't exists"));

        if(productDto.getAvailableStock()<1){
            throw new Exception("please ensure with at least 1 stock for product");
        }
        Product productToSave = Product.builder()
                .productName(productDto.getProductName())
                .productDescription(productDto.getProductDescription())
                .productDiscountedPrice(productDto.getProductDiscountedPrice())
                .productActualPrice(productDto.getProductActualPrice())
                .availableStock(productDto.getAvailableStock())
//                .image(productDto.getImage())
//                .productImages(productDto.getProductImages())

                .uploadId(productDto.getUploadId())

                .category(category)
                .build();
                product.save(productToSave);
            responce.setMessage("Success");
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<AddProductResponse>() {
        });

    }

    @Override
    public ApiResponse<AddProductResponse> updateProduct(UpdateProductRequest req) throws Exception {

        AddProductResponse responce= new AddProductResponse();

        if(req.getCategoryId()== null){
            throw new Exception("please ensure valid category");
        }
        FileUpload fileUpload = fileUploadRepository.findByUploadID(req.getUploadId())
                .orElseThrow(() -> new RecordNotFoundException("image id not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new Exception("category doesn't exists"));

        Product dbProduct = productDao.findById(req.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("product not present"));

        if(req.getAvailableStock()<1){
            throw new Exception("please ensure with at least 1 stock for product");
        }
        dbProduct.setProductName(req.getProductName());
        dbProduct.setProductDescription(req.getProductDescription());
        dbProduct.setProductDiscountedPrice(req.getProductDiscountedPrice());
        dbProduct.setProductActualPrice(req.getProductActualPrice());
        dbProduct.setAvailableStock(req.getAvailableStock());

        dbProduct.setUploadId(req.getUploadId());
        dbProduct.setCategory(category);

        productDao.save(dbProduct);

        responce.setMessage("Success");
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<AddProductResponse>() {
        });
    }

    @Override
    public ApiResponse<List<ProductResponse>> getAllProducts(String searchKey) {

        List<ProductResponse> response= new ArrayList<ProductResponse>();

        List<Product> dbProducts= null;


        if(searchKey== null || searchKey.isEmpty() || searchKey.isBlank()) {
            dbProducts = (List<Product>) productDao.findAll();
            dbProducts.forEach(dbProduct -> {
                Optional<FileUpload> dbFileUploadForProduct = fileUploadRepository.findById(dbProduct.getUploadId());

                ProductResponse product = new ProductResponse();
                product.setProductId(dbProduct.getProductId());
                product.setProductDescription(dbProduct.getProductDescription());
                product.setProductName(dbProduct.getProductName());
                product.setCategory(dbProduct.getCategory());
                product.setProductActualPrice(dbProduct.getProductActualPrice());
                product.setProductDiscountedPrice(dbProduct.getProductDiscountedPrice());
                product.setImageUrl(helperUtils.getCompleteImage() + dbFileUploadForProduct.get().getPathURL());
                product.setImage(helperUtils.getCompleteImage() + dbFileUploadForProduct.get().getPathURL());
                product.setWishList(false);
                product.setUploadId(dbProduct.getUploadId());
                product.setAvailableStock(dbProduct.getAvailableStock());
                response.add(product);
            });

            return ResponseUtils.createSuccessResponse(response, new TypeReference<List<ProductResponse>>() {
            });
        }else{
            dbProducts = productDao
                    .findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase
                            (
                                    searchKey , searchKey
                            );
            System.out.println(dbProducts.size());
            List<Product> withImage= new ArrayList<>();
            dbProducts.forEach(dbProduct->{
                FileUpload dbFileUploadForProduct = null;
                try {
                    dbFileUploadForProduct = fileUploadRepository.findById(dbProduct.getUploadId())
                            .orElseThrow(()->new Exception("no image url found"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

//                byte[] file = fetchImage.getFile(dbFileUploadForProduct.getPathURL());
//                dbProduct.setImage(file);

//                withImage.add(dbProduct);

            });



            return ResponseUtils.createSuccessResponse(response, new TypeReference<List<ProductResponse>>() {});
        }
//        return null;
    }

    @Override
    public ApiResponse<Long> countProducts() {
        long count = productDao.count();
        return ResponseUtils.createSuccessResponse(count, new TypeReference<Long>() { });
    }


}
