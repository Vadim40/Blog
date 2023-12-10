package com.example.blog.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "images")
@SQLDelete(sql = "UPDATE images SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String type;

    private boolean deleted = false;

    @Column(name = "image_data", columnDefinition = "bytea")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "article_id" )
    private Article article;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}