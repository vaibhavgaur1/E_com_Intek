package com.e_commerce.services.impl;

import com.e_commerce._util.Bill;
import com.e_commerce._util.HelperUtils;
import com.e_commerce._util.JwtUtil;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.dao.*;
import com.e_commerce.entity.*;
import com.e_commerce.exception.RecordNotFoundException;
import com.e_commerce.request.OrderInput;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.OrderDetailService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailDao orderDetailDao;
    private final ProductDao productDao;
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final HelperUtils helperUtils;
    private final CartDao cartDao;
    private final UserOrderDao userOrderDao;
    private final Bill billGenerator;
    private final StoreDao storeDao;



    private static final String ORDER_PLACED= "Placed";

    public ApiResponse<OrderDetail> placeOrder(OrderInput orderInput, String authToken, Boolean isSingleProductCheckout) throws Exception {
        List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();
        User dbUser = helperUtils.getUserFromAuthToken(authToken);
//        List<OrderDetail> detailList= new ArrayList<>();

        Store dbStore = storeDao.findById(orderInput.getStoreId())
                .orElseThrow(() -> new RecordNotFoundException("store not present"));

        if( !dbStore.isActive() ) {
            throw new Exception("store is inactive currently");
        }

        AtomicBoolean allGood = new AtomicBoolean(true);
        productQuantityList.forEach(productQuantity-> {
            boolean b = productDao.existsById(productQuantity.getProductId());
            if(b){
                allGood.set(true);
                return;
            }
        });
        if(allGood.get() == false){
            throw new Exception("product not found");
        }

        double totalProductAmount= 0.0, totalAmount= 0.0;

        for (OrderProductQuantity orderProductQuantity : productQuantityList) {
            Product product = productDao.findById(orderProductQuantity.getProductId()).get();

            if( product.getAvailableStock() < orderProductQuantity.getQuantity() ) {
                throw new Exception("product: "+ product.getProductName()+" is not available in required stock");
            }
            synchronized (this){
                //reduce the quantity for product
                product.setAvailableStock(product.getAvailableStock()-orderProductQuantity.getQuantity());
                productDao.save(product);
            }
            totalProductAmount= orderProductQuantity.getQuantity() * product.getProductDiscountedPrice();
            totalAmount+= totalProductAmount;
            orderProductQuantity.setTotalProductAmount(totalProductAmount);

        }
        OrderDetail orderDetail = OrderDetail.builder()
                .orderId(HelperUtils.generateOrderId())
                .orderByName(orderInput.getFullName())
                .deliveryAddress(orderInput.getFullAddress())
                .selectedStore(orderInput.getSelectedStore())
                .contactNo(orderInput.getContactNumber())
                .alternateContactNumber(orderInput.getAlternateContactNumber())
                .orderStatus(ORDER_PLACED)
                .totalOrderAmount(totalAmount)
                .user(dbUser)
                .store(dbStore)
                .orderDate(new Date(System.currentTimeMillis()))
                .build();

        OrderDetail savedOrderDetail = orderDetailDao.save(orderDetail);
        List<UserOrders> userOrdersList= new ArrayList<>();

        productQuantityList.forEach(orderProductQuantity -> {

            Product dbProduct;
            try {
                dbProduct = productDao.
                        findById(orderProductQuantity.getProductId()).get();
            } catch (Exception e) {
                throw new RuntimeException("no product found");
            }
            UserOrders userOrders = UserOrders.builder()
                    .product(dbProduct)
                    .quantity(orderProductQuantity.getQuantity())
                    .productTotalAmount(orderProductQuantity.getTotalProductAmount())
                    .orderDetail(savedOrderDetail)
                    .build();

            UserOrders savedUserOrders = userOrderDao.save(userOrders);
            userOrdersList.add(savedUserOrders);

            List<Cart> dbListCart = cartDao.findByUser(dbUser);
            if(!isSingleProductCheckout){
                dbListCart.forEach(cartDao::delete);
            }
            else{
                dbListCart.stream().filter(cart -> cart.getProduct().getProductId() == dbProduct.getProductId()).forEach(cartDao::delete);
            }

//            detailList.add(orderDetailDao.save(orderDetail));
        });
        savedOrderDetail.setUserOrders(userOrdersList);
        String pathForPdf = billGenerator.generateBillByteArray(savedOrderDetail);
        orderDetailDao.setPdfUrl(savedOrderDetail.getId(), helperUtils.getPathForPdf()+savedOrderDetail.getOrderId()+".pdf");
        savedOrderDetail.setPdfUrl(pathForPdf);

        orderDetail.setUserOrders(userOrdersList);

//        byte[] pdfBytes = billGenerator.generateBillByteArray(orderDetail);
//
//        Map<String, Object> map= new HashMap<>();
//        map.put("orderDetails", orderDetail);
//        map.put("pdfName", orderDetail.getOrderId());
//        map.put("pdfByte", pdfBytes);

        return ResponseUtils.createSuccessResponse(savedOrderDetail, new TypeReference<OrderDetail>() {});
    }


    public ApiResponse<List<OrderDetail>> getOrderDetailsOfUser(String authToken) throws Exception {
        User dbUser = helperUtils.getUserFromAuthToken(authToken);

//        orderDetailDao.findAll(Sort.by(Sort.Direction.DESC, ""));
        Sort sort= Sort.by(Sort.Direction.DESC, "orderDate");
        List<OrderDetail> byUser = orderDetailDao.findByUser(dbUser, sort);

//        userOrderDao.findByOrderDetailId();
//        orderDetailDao
        return ResponseUtils.createSuccessResponse(byUser, new TypeReference<List<OrderDetail>>() {});

    }

    public ApiResponse<List<OrderDetail>> getAllOrderDetailsOfAdmin(String status) {

        if(status.equalsIgnoreCase("ALL"))
            return ResponseUtils.createSuccessResponse(orderDetailDao.findAll(), new TypeReference<List<OrderDetail>>() {});
        else {
            return ResponseUtils.createSuccessResponse(orderDetailDao.findByOrderStatus(status), new TypeReference<List<OrderDetail>>() {});
        }
    }

    public void markOrderAsDelivered(Integer orderId) throws Exception {
        OrderDetail dbOrderDetail = orderDetailDao.findById(orderId)
                .orElseThrow(() -> new Exception("order not found!!"));

        dbOrderDetail.setOrderStatus("DELIVERED");
        orderDetailDao.save(dbOrderDetail);
    }
    public void markOrderAsNotDelivered(Integer orderId) throws Exception {
        OrderDetail dbOrderDetail = orderDetailDao.findById(orderId)
                .orElseThrow(() -> new Exception("order not found!!"));

        dbOrderDetail.setOrderStatus("NOT_DELIVERED");
        orderDetailDao.save(dbOrderDetail);
    }
    @SneakyThrows
    public ApiResponse<List<Object>> getPdf(String orderId) {
        System.out.println(orderId);
        OrderDetail orderDetail = orderDetailDao.findByOrderId(orderId)
                .orElseThrow(() -> new Exception("order not found!!"));
        System.out.println(orderDetail.getPdfUrl());
        Map<String, Object> response = new HashMap<>();



      String pdfBytes = billGenerator.generateBillByteArray(orderDetail);
        response.put("pdfBytes", pdfBytes);
        response.put("PdfName", "PdfGenerated");
        return ApiResponse.<List<Object>>builder()
                .response(List.of(pdfBytes, "pdfName"))
                .build();
    }

    @SneakyThrows
    @Override
    public void cancelOrder(Integer orderId) {
        OrderDetail dbOrderDetail = orderDetailDao.findById(orderId)
                .orElseThrow(() -> new Exception("order not found!!"));

        // increased the quantity after cancelling the order
        List<UserOrders> userOrders = dbOrderDetail.getUserOrders();
        userOrders.forEach(userOrder -> {
            Product product = userOrder.getProduct();
            product.setAvailableStock(product.getAvailableStock()+userOrder.getQuantity());
            productDao.save(product);
        });

        dbOrderDetail.setOrderStatus("CANCELLED");
        orderDetailDao.save(dbOrderDetail);
    }

    @Override
    public ApiResponse<List<OrderDetail>> getStoreOrders(Long storeId) {
        List<OrderDetail> orderDetailList = orderDetailDao.findByStoreId(storeId);
        return ResponseUtils.createSuccessResponse(orderDetailList, new TypeReference<List<OrderDetail>>() {
        });
    }

    @Override
    public ApiResponse<Long> countStoreOrders(Long storeId) {
        return ResponseUtils.createSuccessResponse(orderDetailDao.countByStoreId(storeId),
                new TypeReference<Long>() {});


    }

    @Override
    public ApiResponse<List<OrderDetail>> getOrderDetailsOfUserByUserId(Integer userId) {

        return ResponseUtils.createSuccessResponse(orderDetailDao.findByUserId(userId),
                new TypeReference<List<OrderDetail>>() {});
    }
}
