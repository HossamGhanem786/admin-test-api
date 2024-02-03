package com.santechture.api.service;

import com.santechture.api.configuration.TokenProvider;
import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.dto.admin.AdminDto;
import com.santechture.api.entity.Admin;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.repository.AdminRepository;
import com.santechture.api.validation.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminService {


    private final AdminRepository adminRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    public static final String LOGIN_CREDENTIALS_NOT_MATCH = "login.credentials.not.match";


    public ResponseEntity<GeneralResponse> login(LoginRequest request) throws BusinessExceptions {

        Admin admin = adminRepository.findByUsernameIgnoreCase(request.getUsername());
        if (Objects.isNull(admin) || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessExceptions(LOGIN_CREDENTIALS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(admin.getUsername(), admin.getPassword(), Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);

        AdminDto adminDto = new AdminDto(admin);
        adminDto.setToken(token);
        return new GeneralResponse().response(adminDto);
    }
}
