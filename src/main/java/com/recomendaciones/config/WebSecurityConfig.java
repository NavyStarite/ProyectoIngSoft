package com.recomendaciones.config;

import com.recomendaciones.security.jwt.AuthEntryPointJwt;
import com.recomendaciones.security.jwt.AuthTokenFilter;
import com.recomendaciones.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.and())
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Deshabilitar el formulario de login por defecto de Spring Security
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso a las rutas de autenticación API
                .requestMatchers("/api/auth/**").permitAll()
                
                // Permitir acceso a las páginas web sin autenticación
                .requestMatchers("/", "/home", "/login", "/register").permitAll()
                
                // Permitir acceso a recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                
                // Permitir acceso a endpoints de prueba
                .requestMatchers("/api/test/**").permitAll()
                
                // Las páginas del dashboard requieren autenticación
                .requestMatchers("/dashboard").authenticated()
                
                // Todas las demás rutas de API requieren autenticación
                .requestMatchers("/api/**").authenticated()
                
                // Cualquier otra petición puede pasar (para manejar errores 404, etc.)
                .anyRequest().permitAll()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}