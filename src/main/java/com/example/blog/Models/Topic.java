package com.example.blog.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "topics")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE topics SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "topicsOfInterest")
    private List<User> interestedUsers = new ArrayList<>();

    private  boolean deleted=false;

    @ManyToMany(mappedBy = "topics")
    private List<Article> articles=new ArrayList<>();
}
