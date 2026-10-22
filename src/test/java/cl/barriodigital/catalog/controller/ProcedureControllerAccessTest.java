package cl.barriodigital.catalog.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import cl.barriodigital.catalog.config.SecurityConfig;
import cl.barriodigital.catalog.service.ProcedureService;

/** Quién puede activar y desactivar tipos de trámite, con tokens simulados. */
@SpringJUnitWebConfig(classes = ProcedureControllerAccessTest.TestConfig.class)
class ProcedureControllerAccessTest {

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({ SecurityConfig.class, ProcedureController.class })
    static class TestConfig {

        @Bean
        JwtDecoder jwtDecoder() {
            return token -> {
                throw new BadJwtException("decoder de prueba");
            };
        }

        @Bean
        ProcedureService procedureService() {
            return mock(ProcedureService.class);
        }
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProcedureService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(service);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    /** Token con el claim roles y la autoridad que usa @PreAuthorize. */
    private static RequestPostProcessor token(String role) {
        return jwt()
                .jwt(jwt -> jwt.claim("roles", List.of(role)))
                .authorities(new SimpleGrantedAuthority(role));
    }

    @Test
    void elAdminPuedeActivarUnTipo() throws Exception {
        mockMvc.perform(put("/api/catalog/procedures/3/activate").with(token("Admin")))
                .andExpect(status().isNoContent());

        verify(service).activate(3L);
    }

    @Test
    void elAdminPuedeDesactivarUnTipo() throws Exception {
        mockMvc.perform(delete("/api/catalog/procedures/3").with(token("Admin")))
                .andExpect(status().isNoContent());

        verify(service).deactivate(3L);
    }

    @Test
    void operadorYClienteNoPuedenActivar() throws Exception {
        mockMvc.perform(put("/api/catalog/procedures/3/activate").with(token("Operador")))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/catalog/procedures/3/activate").with(token("Cliente")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }

    @Test
    void sinTokenSeResponde401() throws Exception {
        mockMvc.perform(put("/api/catalog/procedures/3/activate"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }
}