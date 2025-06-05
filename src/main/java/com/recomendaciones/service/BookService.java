package com.recomendaciones.service;

import com.recomendaciones.model.Book;
import com.recomendaciones.model.User;
import com.recomendaciones.repository.BookRepository;
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
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getRecommendationsForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        
        Optional<User> user = userRepository.findById(userDetails.getId());
        if (user.isPresent()) {
            Set<Book> favorites = user.get().getFavoriteBooks();
            // Lógica de recomendación basada en géneros favoritos
            if (!favorites.isEmpty()) {
                String favoriteGenre = favorites.iterator().next().getGenre();
                return bookRepository.findByGenre(favoriteGenre);
            }
        }
        return bookRepository.findAll();
    }
}