package com.example.blog.Services;

import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.User;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Impementations.CustomUserDetailsService;
import com.example.blog.Services.Impementations.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        Set<User> followers = new HashSet<>();
        followers.add(follower);
        User user = User.builder()
                .username("sea")
                .followers(followers)
                .build();
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.ofNullable(user));
        Set<User> foundFollowers = userService.findFollowers("sea");
        Assertions.assertThat(foundFollowers.size()).isEqualTo(1);
    }

    @Test
    public void toggleFollowStatus_Test() {
        User user = User.builder()
                .username("the core")
                .following(new HashSet<>())
                .build();
        User userToSubscribe = User.builder()
                .username("earshot")
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userToSubscribe));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleFollowStatus(1L);

        Assertions.assertThat(user.getFollowing().size()).isEqualTo(1);
    }


    @Test
    public void saveUserTest() {
        User user = new User();
        user.setPassword("24434");
        when(passwordEncoder.encode(any(String.class))).thenReturn("1111");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.saveUser(user);

        Assertions.assertThat(user.getRoles().contains(Role.USER)).isEqualTo(true);
        Assertions.assertThat(user.getCreationDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(user.getPassword()).isEqualTo("1111");
    }

    @Test
    public void updateUser_UserRole_Test() {
        long userId = 1L;
        User authenticatedUser = new User();
        authenticatedUser.getRoles().add(Role.USER);
        authenticatedUser.setId(userId);

        User user = new User();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserById(user, userId);

        Assertions.assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    public void updateUser_AdminRole_Test() {
        long userId = 1L;
        User authenticatedUser = new User();
        authenticatedUser.getRoles().add(Role.ADMIN);

        User user = new User();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserById(user, userId);

        Assertions.assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    public void updateUser_ThrowsException_Test() {
        long userId = 1L;
        User authenticatedUser = new User();
        authenticatedUser.getRoles().add(Role.USER);
        authenticatedUser.setId(10L);
        User user = new User();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(authenticatedUser);

        Assertions.assertThatThrownBy(() -> userService.updateUserById(user, userId))
                .isInstanceOf(AccessDeniedException.class);


    }
}
