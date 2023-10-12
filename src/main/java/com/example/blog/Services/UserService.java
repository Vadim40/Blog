package com.example.blog.Services;

import com.example.blog.Excteptions.UserNotFoundException;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
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
    public Set<User> findFollowers(long userId) {
        User user = findUserById(userId);
        return user.getFollowers();
    }

    @Override
    public Set<User> findSubscriptions(long userId) {
        User user = findUserById(userId);
        return user.getSubscriptions();
    }

    @Override
    public void subscribe(long userToSubscribeId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        User userToSubscribe = findUserById(userToSubscribeId);
        if (authenticationUser.getSubscriptions().contains(userToSubscribe)) {
            throw new IllegalArgumentException("You are already subscribed to this user.");
        }
        authenticationUser.getSubscriptions().add(userToSubscribe);
        userRepository.save(authenticationUser);
    }

    @Override
    public void unsubscribe(long userToUnsubscribeId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        User userToUnsubscribe = findUserById(userToUnsubscribeId);
        if (!authenticationUser.getSubscriptions().contains(userToUnsubscribe)) {
            throw new IllegalArgumentException("You are not subscribed to the user you are trying to unsubscribe from.");
        }
        authenticationUser.getSubscriptions().remove(userToUnsubscribe);
        userRepository.save(authenticationUser);
    }



    public void setAvatar(Image image) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        authenticatedUser.setImage(image);
        userRepository.save(authenticatedUser);
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found."));
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
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
    public void deleteUserById(long userId) {
        checkUserAccess(userId);
        userRepository.deleteById(userId);
    }

    private void checkUserAccess(long userId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        if (!authenticatedUser.getRole().equals(Role.ADMIN) && authenticatedUser.getId() != userId) {
            throw new AccessDeniedException("You don't have permission to update this user");
        }
    }
}
