package com.example.blog.Models.DTOs;

import jakarta.validation.constraints.Max;
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
    @Max(value = 30,message = "no more than 30 symbols")
    String name;
}
