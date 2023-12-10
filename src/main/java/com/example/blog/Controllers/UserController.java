package com.example.blog.Controllers;

import com.example.blog.Mappers.ImageMapper;
import com.example.blog.Mappers.UserMapper;
import com.example.blog.Models.DTOs.UserDTO;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import com.example.blog.Services.Impementations.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    @GetMapping("/search/users")
    public String findUsersByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {
        Page<User> users = userService.findUsersByUsernameIsContainingIgnoreCase(username, pageSize, pageNumber);
        Page<UserDTO> userDTOS = users.map(userMapper::mapToDTO);
        model.addAttribute("users", userDTOS);
        return "users";
    }


    @GetMapping("/@{username}")
    public String findUserByUsername(
            @PathVariable String username,
            Model model) {
        User user = userService.findUserByUsername(username);
        UserDTO userDTO = userMapper.mapToDTO(user);
        model.addAttribute("user", userDTO);
        model.addAttribute("isSubscribed", userService.isSubscribedToUser(username));
        return "show";
    }

    @GetMapping("/@{username}/followers")
    public String findFollowers(
            @PathVariable String username,
            Model model) {
        Set<User> followers = userService.findFollowers(username);
        Set<UserDTO> followersDTO = (Set<UserDTO>) followers.stream().map(userMapper::mapToDTO);
        model.addAttribute("followers", followersDTO);
        return "followers";

    }

    @GetMapping("/@{username}/following")
    public String findSubscriptions(
            @PathVariable String username,
            Model model) {
        Set<User> followers = userService.findFollowing(username);
        Set<UserDTO> followersDTO = (Set<UserDTO>) followers.stream().map(userMapper::mapToDTO);
        model.addAttribute("followers", followersDTO);
        return "followers";

    }

    @PutMapping("/{userId}/toggle-subscriptions")
    public void toggleSubscriptions(
            @PathVariable long userId) {
        userService.toggleFollowStatus(userId);
    }

    @PutMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam MultipartFile file) {
        Image image;
        try {
            image = imageMapper.mapToEntityFromMultipartFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User user = userService.setAvatar(image);
        return "redirect:/users/" + user.getId();
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute @Valid UserDTO userDTO,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "some";
        }
        User user = userMapper.mapToEntity(userDTO);
        user = userService.saveUser(user);
        return "redirect:/users/" + user.getId();
    }

    @PutMapping("/{userId}/update")
    public String updateUser(
            @PathVariable long userId,
            @ModelAttribute @Valid UserDTO userDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "some";
        }
        User user = userMapper.mapToEntity(userDTO);
        userService.updateUserById(user, userId);
        return "redirect:/users/" + user.getId();
    }

    @DeleteMapping("/{userId}/delete")
    public String deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
        return "delete";
    }

}