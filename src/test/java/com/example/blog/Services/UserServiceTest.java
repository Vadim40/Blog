package com.example.blog.Services;

import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.User;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Implementations.CustomUserDetailsService;
import com.example.blog.Services.Implementations.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void findFollowersTest() {
        User follower = User.builder()
                .username("asdff")
                .build();
        List<User> followers = new ArrayList<>();
        followers.add(follower);
        User user = User.builder()
                .username("sea")
                .followers(followers)
                .build();
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.ofNullable(user));
        Pageable pageable = PageRequest.of(0, 3);
        Page<User> foundFollowers = userService.findFollowers("sea", pageable);
        Assertions.assertThat(foundFollowers.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void toggleFollowStatus_Test() {
        User user = User.builder()
                .username("the core")
                .following(new ArrayList<>())
                .build();
        User userToSubscribe = User.builder()
                .username("earshot")
                .followers(new ArrayList<>())
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.ofNullable(userToSubscribe));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleFollowStatus("the core");

        Assertions.assertThat(user.getFollowing().size()).isEqualTo(1);
    }


    @Test
    public void saveUserTest() {
        User user = new User();
        user.setPassword("24434");
        when(passwordEncoder.encode(any(String.class))).thenReturn("1111");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.saveUser(user);

        Assertions.assertThat(user.getRoles().contains(Role.ROLE_USER)).isEqualTo(true);
        Assertions.assertThat(user.getCreationDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(user.getPassword()).isEqualTo("1111");
    }

    @Test
    public void updateUser_UserRole_Test() {
        long userId = 1L;
        User authenticatedUser = new User();
        authenticatedUser.getRoles().add(Role.ROLE_USER);
        authenticatedUser.setUsername("username");
        authenticatedUser.setId(userId);

        User user = new User();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserByUsername(user, "username");

        Assertions.assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    public void updateUser_AdminRole_Test() {
        long userId = 1L;
        User authenticatedUser = new User();
        authenticatedUser.getRoles().add(Role.ROLE_ADMIN);
        authenticatedUser.setId(userId);

        User user = new User();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserByUsername(user, "username");

        Assertions.assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    public void updateUser_ThrowsException_Test() {
        User authenticatedUser = new User();
        authenticatedUser.getRoles().add(Role.ROLE_USER);
        authenticatedUser.setId(10L);
        authenticatedUser.setUsername("ava");
        User user = new User();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        Assertions.assertThatThrownBy(() -> userService.updateUserByUsername(user, "username"))
                .isInstanceOf(AccessDeniedException.class);


    }
}
