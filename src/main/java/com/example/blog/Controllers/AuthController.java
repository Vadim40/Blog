package com.example.blog.Controllers;

import com.example.blog.Models.DTOs.JwtRequest;
import com.example.blog.Models.DTOs.JwtResponse;
import com.example.blog.Services.Implementations.UserServiceImpl;
import com.example.blog.Utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserServiceImpl userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<Object> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("wrong password or login", HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails=userService.findUserByUsername(authRequest.getUsername());
        String token =jwtTokenUtils.generateToken(userDetails);
        return new ResponseEntity<>(new JwtResponse(token),HttpStatus.OK);
    }
}
