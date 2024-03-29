package com.example.blog.Controllers;

import com.example.blog.Mappers.ImageMapper;
import com.example.blog.Mappers.UserMapper;
import com.example.blog.Models.DTOs.RegistrationUserDTO;
import com.example.blog.Models.DTOs.UserViewDTO;
import com.example.blog.Models.DTOs.ValidationErrorResponse;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import com.example.blog.Services.Implementations.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    @GetMapping("/search")
    public ResponseEntity<Page<UserViewDTO>> findUsersByUsername(@RequestParam String username,
                                                                 @RequestParam(defaultValue = "20") int pageSize,
                                                                 @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<User> users = userService.findUsersByUsernameIsContainingIgnoreCase(username, pageable);
        if (users.isEmpty()) {
            log.warn("No users found by this username: {}", username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<UserViewDTO> userViewDTOS = users.map(this::mapUserToDTOAndSetState);
        return new ResponseEntity<>(userViewDTOS, HttpStatus.OK);
    }


    @GetMapping("/{username}")
    public ResponseEntity<UserViewDTO> findUserByUsername(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        UserViewDTO userViewDTO = this.mapUserToDTOAndSetState(user);
        return new ResponseEntity<>(userViewDTO, HttpStatus.OK);
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UserViewDTO>> findFollowers(@PathVariable String username,
                                                           @RequestParam(defaultValue = "20") int pageSize,
                                                           @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<User> users = userService.findFollowers(username, pageable);
        if (users.isEmpty()) {
            log.warn("No followers found by this user {}", username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<UserViewDTO> followers = users.map(this::mapUserToDTOAndSetState);
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserViewDTO>> findSubscriptions(@PathVariable String username,
                                                               @RequestParam(defaultValue = "20") int pageSize,
                                                               @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<User> users = userService.findFollowing(username, pageable);
        if (users.isEmpty()) {
            log.warn("No following found by this user {}", username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<UserViewDTO> following = users.map(this::mapUserToDTOAndSetState);
        return new ResponseEntity<>(following, HttpStatus.OK);
    }

    @PutMapping("/{username}/toggle-subscriptions")
    public ResponseEntity<Void> toggleSubscriptions(@PathVariable String username) {
        userService.toggleFollowStatus(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/upload-avatar")
    public ResponseEntity<Void> uploadAvatar(@RequestParam MultipartFile file) {
        Image image;
        try {
            image = imageMapper.mapToImageFromMultipartFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userService.setAvatar(image);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@Valid @RequestBody RegistrationUserDTO registrationUserDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse errorResponse=new ValidationErrorResponse(bindingResult);
            log.warn("validation errors creating user: {}",errorResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.mapRegistrationUserDTOToUser(registrationUserDTO);
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/update")
    public ResponseEntity<Object> updateUser(@PathVariable String username,
                                             @RequestBody @Valid RegistrationUserDTO registrationUserDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse errorResponse=new ValidationErrorResponse(bindingResult);
            log.warn("validation errors updating user: {}",errorResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.mapRegistrationUserDTOToUser(registrationUserDTO);
        userService.updateUserByUsername(user, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{username}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UserViewDTO mapUserToDTOAndSetState(User user) {
        boolean isFollowingUser = userService.isFollowingUser(user.getUsername());
        return userMapper.mapToUserViewDTO(user, isFollowingUser);
    }


    private Pageable createPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber - 1, pageSize);
    }

}
