package com.example.blog.Models.DTOs;


import com.example.blog.Validators.Annotations.UniqueName;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicDTO {
    long id;
    @Size(max = 30, message = "no more than 30 symbols")
    @UniqueName
    String name;
}
