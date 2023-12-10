package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.UserDTO;
import com.example.blog.Models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .id(user.getId())
                .creationDate(user.getCreationDate())
                .password(user.getPassword())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .selfDescription(user.getSelfDescription())
                .username(user.getUsername())
                .build();
    }
    public User mapToEntity(UserDTO userDTO){
        return User.builder()
                .id(userDTO.getId())
                .creationDate(userDTO.getCreationDate())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .firstname(userDTO.getFirstname())
                .lastname(userDTO.getLastname())
                .selfDescription(userDTO.getSelfDescription())
                .username(userDTO.getUsername())
                .build();
    }
}
