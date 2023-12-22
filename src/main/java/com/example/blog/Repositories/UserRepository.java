package com.example.blog.Repositories;

import com.example.blog.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByArticlesId(long articleId);

    Optional<User> findUserByCommentsId(long commentId);

    Optional<User> findUserByUsername(String username);

    Page<User> findUsersByUsernameIsContainingIgnoreCase(String username, Pageable pageable);
    void deleteUserByUsername(String username);

    boolean existsByUsernameIgnoreCase(String name);
}
