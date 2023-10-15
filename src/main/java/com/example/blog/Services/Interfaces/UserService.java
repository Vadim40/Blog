package com.example.blog.Services.Interfaces;

import com.example.blog.Models.User;

import java.util.Set;

public interface UserService {
    User findUserByArticleId(long articleId);

    User findUserByCommentId(long commentId);

    Set<User> findFollowers(long userId);

    Set<User> findSubscriptions(long userId);

    void subscribe(long userToSubscribeId);

    void unsubscribe(long userToUnsubscribeId);



    User findUserById(long userId);

    User saveUser(User user);

    User updateUserById(User user, long userId);

    void deleteUserById(long id);
}
