package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.UserInfo;
import org.example.eventProducer.UserInfoEvent;
import org.example.eventProducer.UserInfoProducer;
import org.example.model.LoginRequestDTO;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    public UserDetails loadUserByUsername(String username){
        UserInfo user = userRepository.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User not found...!!");
        }
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(String username){
        return userRepository.findByUsername(username);
    }

    public Boolean loginUser(LoginRequestDTO loginRequestDTO){
        UserInfo user = checkIfUserAlreadyExist(loginRequestDTO.getUsername());
        if(user!=null){
            boolean isPasswordMatch = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
            if(isPasswordMatch){
                return true;
            }else{
                throw new BadCredentialsException("Invalid username or password");
            }
        }else{
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Boolean signupUser(UserInfoDto userInfoDto){
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto.getUsername()))){
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId, userInfoDto.getUsername(),userInfoDto.getPassword(), new HashSet<>()));
        //event produce here
        userInfoProducer.sendEventToKafka(userInfoEventToPublish(userInfoDto, userId));
        return true;
    }

    private UserInfoEvent userInfoEventToPublish(UserInfoDto userInfoDto, String userId){
        return UserInfoEvent.builder()
                .userId(userId)
                .firstName(userInfoDto.getUsername())
                .lastName(userInfoDto.getLastName())
                .email(userInfoDto.getEmail())
                .phoneNumber(userInfoDto.getPhoneNumber()).build();

    }
}
