package com.recomendaciones.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "movies")
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String director;
    private String genre;
    private String description;
    private Integer releaseYear;
    private Double averageRating;
    public String getGenre() {
        return genre;
    }
}