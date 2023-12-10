package com.example.blog.Services.Impementations;

import com.example.blog.Excteptions.UserNotFoundException;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User findUserByArticleId(long articleId) {
        return userRepository.findUserByArticlesId(articleId).orElseThrow(
                () -> new UserNotFoundException("User not found."));
    }

    @Override
    public User findUserByCommentId(long commentId) {
        return userRepository.findUserByCommentsId(commentId).orElseThrow(
                () -> new UserNotFoundException("User not found."));
    }

    @Override
    public Page<User> findUsersByUsernameIsContainingIgnoreCase(String username, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findUsersByUsernameIsContainingIgnoreCase(username, pageable);
    }

    @Override
    public Set<User> findFollowers(String username) {
        User user = findUserByUsername(username);
        return user.getFollowers();
    }

    @Override
    public Set<User> findFollowing(String username) {
        User user = findUserByUsername(username);
        return user.getFollowing();
    }

    @Override
    public void toggleFollowStatus(long userToSubscribeId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        User userToSubscribe = findUserById(userToSubscribeId);
        if (authenticationUser.getFollowing().contains(userToSubscribe)) {
            authenticationUser.getFollowing().remove(userToSubscribe);
        } else {
            authenticationUser.getFollowing().add(userToSubscribe);
        }
        userRepository.save(authenticationUser);
    }


    @Override
    public User setAvatar(Image image) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        image.setUser(authenticatedUser);
        authenticatedUser.setAvatar(image);
        return userRepository.save(authenticatedUser);
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found."));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User no found"));
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.USER);
        user.setCreationDate(LocalDate.now());
        return userRepository.save(user);
    }

    @Override
    public User updateUserById(User user, long userId) {
        checkUserAccess(userId);
        user.setId(userId);
        return userRepository.save(user);
    }

    @Override
    public boolean isSubscribedToUser(String username) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        User user = findUserByUsername(username);
        return authenticatedUser.getFollowing().contains(user);
    }

    @Override
    public void deleteUserById(long userId) {
        checkUserAccess(userId);
        userRepository.deleteById(userId);
    }

    private void checkUserAccess(long userId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        if (!authenticatedUser.getRoles().contains(Role.ADMIN) && authenticatedUser.getId() != userId) {
            throw new AccessDeniedException("You don't have permission to perform this action on this this user");
        }
    }
}
