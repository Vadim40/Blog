package com.example.blog.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserViewDTO {
    private UserDTO userDTO=new UserDTO();
    private boolean isFollowed ;
}
