package com.e_commerce.services.impl;

import com.e_commerce.Dto.AddressDto;
import com.e_commerce.Dto.OtpDto;
import com.e_commerce.Dto.RegisterDto;
import com.e_commerce.Dto.UserDto;
import com.e_commerce._util.EmailService;
import com.e_commerce._util.HelperUtils;
import com.e_commerce._util.OTPGenerator;
import com.e_commerce._util.ResponseUtils;
import com.e_commerce.dao.AddressDao;
import com.e_commerce.dao.OtpDao;
import com.e_commerce.dao.RoleDao;
import com.e_commerce.dao.UserDao;
import com.e_commerce.entity.Address;
import com.e_commerce.entity.Otp;
import com.e_commerce.entity.Role;
import com.e_commerce.entity.User;
import com.e_commerce.exception.RecordNotFoundException;
import com.e_commerce.response.ApiResponse;
import com.e_commerce.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpDao otpRepository;
    private  final AddressDao addressDao;
    private final UserDao userDao;
    private final HelperUtils helperUtils;

    public ApiResponse<User> registerNewUser(RegisterDto registerDto) throws Exception {
try {
        List<Otp> otpbyEmail = otpRepository.findByEmail(registerDto.getEmail());
        if (otpbyEmail.isEmpty()) {
            throw new Exception("your email: " + registerDto.getEmail() + " has not been verified");
        }
        if (!otpbyEmail.get(0).isVerified())
            throw new Exception("your email: " + registerDto.getEmail() + " has not been verified");

//        List<User> dbListUser = userDao.findByLiquorCardNumberOrGroceryCardNumber(registerDto.getName());
//        if(!dbListUser.isEmpty())
//            throw new Exception("User with id: "+registerDto.getName()+" is already present");

        List<User> userCard1 = userDao.findByLiquorCardNumber(registerDto.getLiquorCardNumber());
        if (!userCard1.isEmpty())
            throw new Exception("User with cardNo.: " + registerDto.getLiquorCardNumber() + " is already present");

        List<User> userCard2 = userDao.findByGroceryCardNumber(registerDto.getGroceryCardNumber());
        if (!userCard2.isEmpty())
            throw new Exception("User with cardNo.: " + registerDto.getGroceryCardNumber() + " is already present");

        List<User> userAdhaar = userDao.findByAdhaar(registerDto.getAdhaar());
        if (!userAdhaar.isEmpty())
            throw new Exception("User with adhaar: " + registerDto.getAdhaar() + " is already present");

        List<User> userPan = userDao.findByPan(registerDto.getPan());
        if (!userPan.isEmpty())
            throw new Exception("User with pan: " + registerDto.getPan() + " is already present");

        Role dbRole = roleDao.findById("USER").orElseThrow(() -> new Exception("Role not found"));
    List<Role> all = roleDao.findAll();
    System.out.println(all);
    Set<Role> roleSet = new HashSet<>();
        roleSet.add(dbRole);

        User user = User.builder()
                .name(registerDto.getName())
                .contactNumber(registerDto.getContactNumber())
                .adhaar(registerDto.getAdhaar())
                .liquorCardNumber(registerDto.getLiquorCardNumber())
                .dob(registerDto.getDob())
                .email(registerDto.getEmail())
                .pan(registerDto.getPan())
                .groceryCardNumber(registerDto.getGroceryCardNumber())
                .userPassword(passwordEncoder.encode(registerDto.getPassword()))
                .password(registerDto.getPassword())
                .roles(roleSet)
                .otp(registerDto.getOtp())
                .isVerified(true)
                .enabled(true)
                .build();

        User save = userDao.save(user);
        emailService.sendWelcomeMail(user.getOtp(), registerDto.getEmail());
        return ResponseUtils.createSuccessResponse(save, new TypeReference<User>() {});
          }catch(Exception e){
                return ResponseUtils.createFailure(e.getMessage(), 400);
    }
    }


    @SneakyThrows
    public boolean verifyOtp(OtpDto otpDto) {

        List<Otp> otpbyEmail = otpRepository.findByEmail(otpDto.getEmail());
        if(otpbyEmail.isEmpty())
            throw new Exception("otp has not been generated before!!");

        Otp otp = otpbyEmail.get(0);

        if(otp.getOtp().equals(otpDto.getOtp())){
            otp.setVerified(true);
            otpRepository.save(otp);
            return true;
        }else
            return false;
    }

    @Transactional
    public ApiResponse<String> generateOtp(String email) throws MessagingException {
        System.out.println(otpRepository.deleteByEmail(email));;
        Long l = OTPGenerator.generateOtp();
        Otp otp = Otp.builder()
//                81680   DHQ11
                .otp(l)
                .email(email)
                .build();
        otpRepository.save(otp);
        emailService.sendOtpMail(l, email);

        return ResponseUtils.createSuccessResponse("otp has been sent to entered mail", new TypeReference<String>() {});
    }

    @Override
    public ApiResponse disableAccount(String authHeader) throws Exception {

        User dbUser = helperUtils.getUserFromAuthToken((authHeader));
        int i = userDao.enableOrDisableUser(false, dbUser.getId());

        if(i != 0)
            return ResponseUtils.createSuccessResponse("account has been disabled", new TypeReference() {});
        else
            return ResponseUtils.createFailure("something went wrong", HttpStatus.BAD_REQUEST.value());
    }

    @SneakyThrows
    @Override
    public ApiResponse updateUser(UserDto userDto) {

        try{
            User dbUser = userDao.findById(userDto.getId())
                    .orElseThrow(() -> new RecordNotFoundException("user not found"));

            dbUser.setName(userDto.getName());
            dbUser.setPan(userDto.getPan());
            dbUser.setAdhaar(userDto.getAdhaar());
            dbUser.setContactNumber(userDto.getContactNumber());
            dbUser.setLiquorCardNumber(userDto.getLiquorCardNumber());
            dbUser.setGroceryCardNumber(userDto.getGroceryCardNumber());
            dbUser.setDob(userDto.getDob());

            //because of put mapping

            dbUser.setUserPassword(dbUser.getUserPassword());
            dbUser.setEmail(dbUser.getEmail());
            dbUser.setPassword(dbUser.getPassword());
            dbUser.setEnabled(dbUser.isEnabled());
            dbUser.setVerified(dbUser.isVerified());
            dbUser.setOtp(dbUser.getOtp());

            User updatedUser = userDao.save(dbUser);

            return ResponseUtils.createSuccessResponse(updatedUser, new TypeReference() {});
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse enableUser(Integer userId) throws Exception {

        userDao.findById(userId).orElseThrow(()-> new Exception("user doesn't exists"));
        int i = userDao.enableOrDisableUser(true, userId);

        if(i != 0)
            return ResponseUtils.createSuccessResponse("account has been enabled", new TypeReference() {});
        else
            return ResponseUtils.createFailure("something went wrong", HttpStatus.BAD_REQUEST.value());

    }


}
