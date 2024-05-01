package com.e_commerce.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JwtRequest {

//    private String cardNumber;
    private String userName;
    private String userPassword;
//    private String serviceNumber;
    private String userType;                    //user type me ya tho admin h=bhej na ya user admin bhejo ga thp servicenu chk krega else card num
    private String cardType;

}
