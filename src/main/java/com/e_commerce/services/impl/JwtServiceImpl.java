package com.e_commerce.services.impl;

import com.e_commerce._configuration.UserDetailService;
import com.e_commerce._util.JwtUtil;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.dao.UserDao;
import com.e_commerce.entity.Admin;
import com.e_commerce.entity.User;
import com.e_commerce.request.JwtRequest;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.response.JwtResponse;
import com.e_commerce.services.JwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;

    public ApiResponse<JwtResponse> createJwtToken(JwtRequest loginrequest, String cardType) throws Exception {

        String username = loginrequest.getUserName();;
        String password = loginrequest.getUserPassword();;

        /*if(loginrequest.getUserType().equals("ADMIN")){
            username=loginrequest.getServiceNumber();
        }else {
            username = loginrequest.getCardNumber();
        }*/


//        UserDetails userDetails = userDetailService.userDetailsService()
//                .loadUserByUsername(username);
        UserDetails userDetails = null;
        Authentication asdas;
        try{
            asdas= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username,password

            ));
            userDetails = (UserDetails)asdas.getPrincipal();
            System.out.println("Asdads");
        }catch (DisabledException e){
            throw new Exception("user is disabled");
        } catch (BadCredentialsException e) {
            throw new Exception("bad credentials from user");
        }
//        System.out.println("JWT_REQUEST: "+jwtRequest);
//
//        try{
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    jwtRequest.getCardNumber(), jwtRequest.getUserPassword()
//            ));
//        }catch (DisabledException e){
//            throw new Exception("user is disabled");
//        } catch (BadCredentialsException e) {
//            throw new Exception("bad credentials from user");
//        }
//        UserDetails userDetails = userDetailService.userDetailsService()
//                .loadUserByUsername(jwtRequest.getCardNumber()); //+"-"+cardType

        String generatedToken = jwtUtil.generateToken(userDetails);
        if(!loginrequest.getUserType().equalsIgnoreCase("admin")) {
            User user= (User) userDetails;
            System.out.println(user);
            JwtResponse build = JwtResponse.builder()
                    .jwtToken(generatedToken)
                    .user(user)
                    .build();
            return ResponseUtils.createSuccessResponse(build, new TypeReference<JwtResponse>() {});
        } else {
            Admin admin= (Admin) userDetails;
            User user = new User();
            user.setId(admin.getId().intValue());
            user.setContactNumber(admin.getContact());
            user.setEmail(admin.getEmail());
            user.setUserActive(admin.isActive());
            user.setName(admin.getName());
            user.setUserPassword(admin.getPassword());
            user.setGroceryCardNumber(admin.getServiceNo());
            user.setRoles(admin.getRoles());

            JwtResponse build = JwtResponse.builder()
                    .jwtToken(generatedToken)
                    .user(user)
                    .build();
            return ResponseUtils.createSuccessResponse(build, new TypeReference<JwtResponse>() {});
        }



//        User user = userDao.findByLiquorCardNumberOrGroceryCardNumber(jwtRequest.getCardNumber(),jwtRequest.getCardNumber()).get(0);

    }
}
