    package com.example.blog.Models;

    import com.example.blog.Models.Enums.Role;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.Email;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.SQLDelete;
    import org.hibernate.annotations.Where;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDate;
    import java.util.Collection;
    import java.util.HashSet;
    import java.util.Set;
    import java.util.stream.Collectors;

    @Entity
    @Table(name = "users_table")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @SQLDelete(sql = "UPDATE user_table SET deleted = true WHERE id=?")
    @Where(clause = "deleted=false")
    public class User implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column
        private String firstname;

        @Column
        private String lastname;

        @Column
        private String username;

        @Column
        @Email
        private String email;
        @Column
        private  String password ;


        @Column(name = "self_description")
        private String selfDescription;

        @Column(name = "creation_date")
        private LocalDate creationDate;

        @Column
        private boolean deleted=false;
        @ElementCollection
        @CollectionTable(
                name="user_role",
                joinColumns = @JoinColumn(name = "user_id")
        )
        @Column(name="role")
        private Set<Role> roles=new HashSet<>();
        @ManyToMany
        @JoinTable(
                name = "user_subscriptions",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "subscription_id")
        )
        private Set<User> subscriptions=new HashSet<>();

        @ManyToMany(mappedBy = "subscriptions")
        private Set<User> followers=new HashSet<>();
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private Set<Article> articles=new HashSet<>();

        @ManyToMany
        @JoinTable(
                name = "user_saved_articles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "article_id")
        )
        private Set<Article> savedArticles=new HashSet<>();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private Set<Comment> comments=new HashSet<>();

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        private Image image;
        @ElementCollection
        @CollectionTable(
                name = "user_liked_articles",
                joinColumns = @JoinColumn(name = "user_id")
        )
        @Column(name = "article_id")
        private Set<Long> likedArticles=new HashSet<>();
        @ElementCollection
        @CollectionTable(
                name = "user_liked_comments",
                joinColumns = @JoinColumn(name = "user_id")
        )
        @Column(name = "comment_id")
        private Set<Long> likedComments=new HashSet<>();

        @ManyToMany
        @JoinTable(
                name="user_topic_interests",
                joinColumns =@JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name ="topic_id")
        )
        private Set<Topic> topicsOfInterest=new HashSet<>();
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getDisplayName()))
                    .collect(Collectors.toList());
        }



        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }


    }