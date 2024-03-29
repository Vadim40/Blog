package com.example.blog.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "articles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE articles SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String text;
    @Column
    private String title;
    @Column
    private int likes;
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column
    private boolean deleted = false;

    @Column
    private boolean published=false;

    @ManyToMany
    @JoinTable(
            name = "article_topic",
            joinColumns =@JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> topics=new HashSet<>();
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments=new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Image> images=new ArrayList<>();

}
