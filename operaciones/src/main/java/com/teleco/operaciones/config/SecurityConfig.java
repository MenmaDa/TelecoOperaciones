package com.teleco.operaciones.config;

import com.teleco.operaciones.config.JwtAuthenticationFilter;
import com.teleco.operaciones.service.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        .requestMatchers("/uploads/**")
                        .permitAll()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        )
                        .permitAll()

                        // El técnico puede consultar y actualizar únicamente sus tareas.
                        .requestMatchers(
                                "/api/tareas/mis-tareas",
                                "/api/tareas/*/mi-progreso",
                                "/api/notificaciones/**"
                        )
                        .authenticated()

                        // Gestor: lectura de empleados para tareas y recursos.
                        .requestMatchers(HttpMethod.GET, "/api/empleados/**")
                        .hasAnyRole("ADMINISTRADOR", "GESTOR_PROYECTOS")

                        .requestMatchers(HttpMethod.GET, "/api/clientes/**")
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "GESTOR_PROYECTOS",
                                "FACTURACION",
                                "COMERCIAL"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/proyectos/**",
                                "/api/productos-servicios/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "GESTOR_PROYECTOS",
                                "FACTURACION"
                        )

                        // Administración de usuarios.
                        .requestMatchers(
                                "/api/usuarios/**",
                                "/api/roles/**",
                                "/api/sedes/**",
                                "/api/empleados/**",
                                "/api/auditorias/**",
                                "/api/infraestructura/**"
                        )
                        .hasRole("ADMINISTRADOR")

                        // Gestión comercial y de proyectos.
                        .requestMatchers(
                                "/api/clientes/**",
                                "/api/contactos-clientes/**",
                                "/api/contratos/**",
                                "/api/proyectos/**",
                                "/api/tareas/**",
                                "/api/asignaciones-recursos/**"
                        )
                        .hasAnyRole("ADMINISTRADOR", "GESTOR_PROYECTOS")

                        // Catálogo y facturación.
                        .requestMatchers(
                                "/api/productos-servicios/**",
                                "/api/facturas/**",
                                "/api/pagos/**"
                        )
                        .hasAnyRole("ADMINISTRADOR", "FACTURACION")

                        .requestMatchers("/api/recomendaciones/**")
                        .hasAnyRole("ADMINISTRADOR", "GESTOR_PROYECTOS", "FACTURACION", "COMERCIAL")

                        .requestMatchers("/api/reportes/**")
                        .hasAnyRole("ADMINISTRADOR", "FACTURACION", "GESTOR_PROYECTOS")

                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}