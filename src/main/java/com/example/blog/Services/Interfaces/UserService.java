package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User findUserByArticleId(long articleId);

    User findUserByCommentId(long commentId);

    Page<User> findUsersByUsernameIsContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findFollowers(String username, Pageable pageable);

    Page<User> findFollowing(String username, Pageable pageable);

    void toggleFollowStatus(String userToSubscribeId);


    User setAvatar(Image image);

    User findUserById(long userId);

    User findUserByUsername(String username);

    User saveUser(User user);

    User updateUserByUsername(User user, String username);

    boolean isFollowingUser(String username);

    void deleteUserByUsername(String username);

    boolean IsUsernameExists(String username);
}
