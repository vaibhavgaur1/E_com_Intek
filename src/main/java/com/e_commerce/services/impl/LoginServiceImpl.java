package com.e_commerce.services.impl;

import com.e_commerce._configuration.UserDetailService;
import com.e_commerce._util.JwtUtil;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.request.LoginRequest;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.response.LoginResponce;
import com.e_commerce.services.LoginService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    @Autowired
    private  JwtUtil jwtUtil;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private  UserDetailService userDetailService;

    @Override
    public ApiResponse<LoginResponce> getLoginData(LoginRequest loginrequest) throws Exception {
        LoginResponce loginResponce = new LoginResponce();
        String username;
        String password;

        if(loginrequest.getUserType().equals("ADMIN")){
            username=loginrequest.getServiceNumber();
        }else {
            username = loginrequest.getCardNo();
        }
        password=loginrequest.getPassword();
        try{
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        username,password
                ));
        }catch (DisabledException e){
            throw new Exception("user is disabled");
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new Exception("bad credentials from user");
        }
        UserDetails userDetails = userDetailService.userDetailsService()
                .loadUserByUsername(username); //+"-"+cardType

        String generatedToken = jwtUtil.generateToken(userDetails);

        loginResponce.setToken(generatedToken);
        loginResponce.setMessage("Success");
        return ResponseUtils.createSuccessResponse(loginResponce, new TypeReference<LoginResponce>() {
        });
    }
}
