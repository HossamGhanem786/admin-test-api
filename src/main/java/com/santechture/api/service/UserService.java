package com.santechture.api.service;

import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.dto.user.UserDto;
import com.santechture.api.entity.User;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.repository.UserRepository;
import com.santechture.api.validation.AddUserRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

// todo we need to create class for constants in util package
    public static final String USERNAME_EXIST = "username.exist";
    public static final String EMAIL_EXIST = "email.exist";
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public ResponseEntity<GeneralResponse> list(Pageable pageable) {
        return new GeneralResponse().response(userRepository.findAll(pageable));
    }

    public ResponseEntity<GeneralResponse> addNewUser(AddUserRequest request) throws BusinessExceptions {

        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new BusinessExceptions(USERNAME_EXIST, HttpStatus.BAD_REQUEST);
        } else if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessExceptions(EMAIL_EXIST, HttpStatus.BAD_REQUEST);
        }

        User user = new User(request.getUsername(), request.getEmail());
        userRepository.save(user);
        SecurityContextHolder.clearContext();
        return new GeneralResponse().response(new UserDto(user));
    }

}
