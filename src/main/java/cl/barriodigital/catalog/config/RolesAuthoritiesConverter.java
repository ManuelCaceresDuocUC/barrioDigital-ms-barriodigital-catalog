package cl.barriodigital.catalog.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Convierte el claim {@code roles} del JWT de Azure AD en authorities, sin prefijo.
 *
 * <p>Un usuario autenticado que no trae ningún rol conocido (por ejemplo, un vecino que se registró
 * solo y aún no tiene rol asignado) se trata como {@value #DEFAULT_ROLE}. Los roles del personal
 * (Admin, Operador, Auditor) solo existen si se asignan en Azure: nunca se otorgan por defecto.
 *
 * <p>QUÉ ES: puente entre el token de Azure y Spring Security. Spring, por defecto, mira el claim "scope";
 * Azure pone los roles en el claim "roles", por eso hace falta este conversor.
 */
public class RolesAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    // Rol que se asume cuando el token no trae ninguno de los conocidos (cumple la regla "sin rol = Cliente").
    static final String DEFAULT_ROLE = "Cliente";

    // Lista blanca de roles reconocidos por la aplicación.
    private static final Set<String> KNOWN_ROLES = Set.of("Cliente", "Operador", "Admin", "Auditor");

    // Conversor estándar de Spring, que aquí se configura para leer el claim "roles".
    private final JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();

    public RolesAuthoritiesConverter() {
        // Lee la lista del claim "roles" del JWT (ej.: ["Admin"]).
        delegate.setAuthoritiesClaimName("roles");
        // Sin prefijo: Spring suele anteponer "SCOPE_" o "ROLE_"; así @PreAuthorize usa 'Admin' directamente.
        delegate.setAuthorityPrefix(""); // 'Admin', 'Operador', 'Cliente', 'Auditor' tal cual
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Roles que trae el token (puede ser null si no hay claim "roles").
        Collection<GrantedAuthority> fromClaim = delegate.convert(jwt);
        List<GrantedAuthority> authorities = new ArrayList<>(fromClaim == null ? List.of() : fromClaim);

        // ¿Alguno de los roles del token es uno de los conocidos?
        boolean hasKnownRole = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(KNOWN_ROLES::contains);
        // Si no, se agrega "Cliente": el usuario podrá usar las funciones básicas, nunca las de personal.
        if (!hasKnownRole) {
            authorities.add(new SimpleGrantedAuthority(DEFAULT_ROLE));
        }
        return authorities;
    }
}
