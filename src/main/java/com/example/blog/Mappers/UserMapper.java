package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.RegistrationUserDTO;
import com.example.blog.Models.DTOs.UserDTO;
import com.example.blog.Models.DTOs.UserViewDTO;
import com.example.blog.Models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ImageMapper imageMapper = new ImageMapper();

    public UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setCreationDate(user.getCreationDate());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        userDTO.setSelfDescription(user.getSelfDescription());
        userDTO.setUsername(user.getUsername());
        userDTO.setAvatar(imageMapper.mapToImageDTO(user.getAvatar()));
        return userDTO;
    }

    public User mapUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setCreationDate(userDTO.getCreationDate());
        user.setEmail(userDTO.getEmail());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setSelfDescription(userDTO.getSelfDescription());
        user.setUsername(userDTO.getUsername());
        user.setAvatar(imageMapper.mapToImage(userDTO.getAvatar()));
        return user;
    }

    public User mapRegistrationUserDTOToUser(RegistrationUserDTO registrationUserDTO) {
        User user = mapUserDTOToUser(registrationUserDTO.getUserDTO());
        user.setPassword(registrationUserDTO.getPassword());
        return user;
    }

    public UserViewDTO mapToUserViewDTO(User user, boolean isFollowingUser) {
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setUserDTO(mapToUserDTO(user));
        userViewDTO.setFollowed(isFollowingUser);
        return userViewDTO;
    }
}
