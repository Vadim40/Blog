package com.example.blog.Controllers;

import com.example.blog.Models.DTOs.JwtRequest;
import com.example.blog.Models.DTOs.JwtResponse;
import com.example.blog.Services.Implementations.UserServiceImpl;
import com.example.blog.Utils.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserServiceImpl userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<Object> createAuthToken(@RequestBody JwtRequest authRequest) {
        Authentication authentication=new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword());
        try {
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            log.warn("authentication failed: invalid credential for user: {}", authRequest.getUsername());
            return new ResponseEntity<>("wrong password or login", HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails=userService.findUserByUsername(authRequest.getUsername());
        String token =jwtTokenUtils.generateToken(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>(new JwtResponse(token),HttpStatus.OK);
    }
//    @PostMapping("/logout")
//    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
//        HttpSession session = request.getSession(false);
//        SecurityContextHolder.clearContext();
//        if (session != null) {
//            session.invalidate();
//        }
//        for (Cookie cookie : request.getCookies()) {
//            cookie.setMaxAge(0);
//        }
//
//        return "logout";
//    }


}
