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
    import java.util.*;
    import java.util.stream.Collectors;

    @Entity
    @Table(name = "users_table")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @SQLDelete(sql = "UPDATE users_table SET deleted = true WHERE id=?")
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
                name = "user_following",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "subscription_id")
        )
        private List<User> following =new ArrayList<>();

        @ManyToMany(mappedBy = "following")
        private List<User> followers=new ArrayList<>();
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<Article> articles=new ArrayList<>();

        @ManyToMany
        @JoinTable(
                name = "user_favorite_articles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "article_id")
        )
        private List<Article> favoriteArticles =new ArrayList<>();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<Comment> comments=new ArrayList<>();

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        private Image avatar;
        @ElementCollection
        @CollectionTable(
                name = "user_liked_articles",
                joinColumns = @JoinColumn(name = "user_id")
        )
        @Column(name = "article_id")
        private List<Long> likedArticles=new ArrayList<>();
        @ElementCollection
        @CollectionTable(
                name = "user_liked_comments",
                joinColumns = @JoinColumn(name = "user_id")
        )
        @Column(name = "comment_id")
        private List<Long> likedComments=new ArrayList<>();

        @ManyToMany
        @JoinTable(
                name="user_topic_interests",
                joinColumns =@JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name ="topic_id")
        )
        private List<Topic> topicsOfInterest=new ArrayList<>();
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
        @Override
        public int hashCode() {
            return Objects.hash(id, username);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            User user = (User) obj;
            return Objects.equals(id, user.id) && Objects.equals(username, user.username);
        }



    }