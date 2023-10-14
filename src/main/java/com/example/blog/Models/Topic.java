package com.example.blog.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topics")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE user_table SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToMany(mappedBy = "topicsOfInterest")
    private Set<User> interestedUsers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "parent_topic_id")
    private  Topic parentTopic;

    @ManyToMany(mappedBy = "topics")
    private Set<Article> articles=new HashSet<>();
}
