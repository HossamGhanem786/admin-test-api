package com.santechture.api.controller;


import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.service.UserService;
import com.santechture.api.validation.AddUserRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping
    public ResponseEntity<GeneralResponse> list(Pageable pageable) {
        return userService.list(pageable);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity<GeneralResponse> addNewUser(@RequestBody AddUserRequest request) throws BusinessExceptions {
        return userService.addNewUser(request);
    }
}
