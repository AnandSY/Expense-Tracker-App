package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.model.LoginRequestDTO;
import org.example.model.UserInfoDto;
import org.example.response.JwtResponseDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@AllArgsConstructor
@RestController
public class AuthController
{

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("auth/v1/signup")
    public ResponseEntity SignUp(@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean isSignUped = userDetailsService.signupUser(userInfoDto);
            if(Boolean.FALSE.equals(isSignUped)){
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());
            return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken).
                    token(refreshToken.getToken()).build(), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("auth/v1/login")
//    public ResponseEntity Login(@RequestBody LoginRequestDTO loginRequestDTO){
//        try{
//            Boolean isLoggedIn = userDetailsService.loginUser(loginRequestDTO);
//            if(isLoggedIn){
//                RefreshToken refreshToken = refreshTokenService.generateRefreshToken(loginRequestDTO.getUsername());
//                String jwtToken = jwtService.GenerateToken(loginRequestDTO.getUsername());
//
//                return new ResponseEntity<>(
//                        JwtResponseDTO.builder()
//                                .accessToken(jwtToken)
//                                .token(refreshToken.getToken())
//                                .build(),
//                        HttpStatus.OK
//                );
//            }else{
//                return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>("Login Exception: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}