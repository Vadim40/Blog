package com.example.blog.Models;


import com.example.blog.Models.Enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE article SET deleted = true WHERE id=?")
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
    @Column
    private LocalDate creationDate;

    @Column
    private boolean deleted = false;
//    @Column
//    @Enumerated
//    private Category category;

//    @ElementCollection
//    @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
//    @Column(name = "tag")
//    private Set<String> tags= new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "article_topic",
            joinColumns =@JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> topics=new HashSet<>();
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<Comment> comments=new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<Image> images=new HashSet<>();

}
