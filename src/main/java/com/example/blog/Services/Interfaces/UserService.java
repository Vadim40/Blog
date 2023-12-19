package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    User findUserByArticleId(long articleId);

    User findUserByCommentId(long commentId);
    Page<User> findUsersByUsernameIsContainingIgnoreCase(String username,Pageable pageable);
    Page<User> findFollowers(String username, Pageable pageable);

    Page<User> findFollowing(String username, Pageable pageable);

    void toggleFollowStatus(long userToSubscribeId);


    User setAvatar(Image image);

    User findUserById(long userId);
    User findUserByUsername(String username);

    User saveUser(User user);

    User updateUserById(User user, long userId);

    boolean isFollowingUser(String username);

    void deleteUserById(long id);
}
