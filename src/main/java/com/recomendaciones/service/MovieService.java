package com.recomendaciones.service;

import com.recomendaciones.model.Movie;
import com.recomendaciones.model.User;
import com.recomendaciones.repository.MovieRepository;
import com.recomendaciones.repository.UserRepository;
import com.recomendaciones.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getRecommendationsForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        
        Optional<User> user = userRepository.findById(userDetails.getId());
        if (user.isPresent()) {
            Set<Movie> favorites = user.get().getFavoriteMovies();
            // Lógica de recomendación basada en géneros favoritos
            if (!favorites.isEmpty()) {
                String favoriteGenre = favorites.iterator().next().getGenre();
                return movieRepository.findByGenre(favoriteGenre);
            }
        }
        return movieRepository.findAll();
    }
}