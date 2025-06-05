package com.recomendaciones.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String author;
    private String genre;
    private String description;
    private Integer publicationYear;
    private Double averageRating;
    public String getGenre() {
        return genre;
    }
}
