package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.UserDTO;
import com.example.blog.Models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ImageMapper imageMapper;

    public UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setCreationDate(user.getCreationDate());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        userDTO.setSelfDescription(user.getSelfDescription());
        userDTO.setUsername(user.getUsername());
        userDTO.setAvatar(imageMapper.mapToDTO(user.getAvatar()));
        return userDTO;
    }

    public User mapToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setCreationDate(userDTO.getCreationDate());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setSelfDescription(userDTO.getSelfDescription());
        user.setUsername(userDTO.getUsername());
        user.setAvatar(imageMapper.mapToEntity(userDTO.getAvatar()));
        return user;
    }
}
