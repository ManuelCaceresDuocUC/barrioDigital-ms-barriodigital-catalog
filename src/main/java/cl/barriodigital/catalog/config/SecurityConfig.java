package cl.barriodigital.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Valida el JWT de Azure AD y convierte el claim {@code roles} en authorities
 * (sin prefijo), igual que ms-barriodigital-requests. Un usuario autenticado sin
 * ningún rol conocido se trata como Cliente (ver {@link RolesAuthoritiesConverter}).
 * La autorización por rol se declara con {@code @PreAuthorize} en los controladores.
 *
 * <p>QUÉ ES: la configuración de seguridad del servicio (autenticación a nivel HTTP + autorización por método).
 */
@Configuration
// Activa las anotaciones @PreAuthorize en los controladores.
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Sin sesiones ni cookies => no hace falta protección CSRF.
                .csrf(AbstractHttpConfigurer::disable)
                // El servidor no guarda estado entre peticiones: cada una trae su JWT.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Primera barrera: toda ruta exige un usuario autenticado (JWT válido). 401 si no.
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // El servicio es "resource server": valida el JWT con las claves públicas del emisor
                // (issuer-uri) y usa nuestro conversor para obtener los roles.
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    // Conversor que toma el JWT validado y construye la autenticación con los roles de RolesAuthoritiesConverter.
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RolesAuthoritiesConverter());
        return converter;
    }
}
