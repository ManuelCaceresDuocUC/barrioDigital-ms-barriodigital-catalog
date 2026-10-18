package cl.barriodigital.catalog.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Reloj de la aplicación en la zona de la comuna. El "día" del cupo se calcula con
 * este reloj, no con UTC: a las 21:00 en Santiago ya es el día siguiente en UTC.
 */
@Configuration
public class ClockConfig {

    public static final ZoneId ZONE = ZoneId.of("America/Santiago");

    @Bean
    Clock clock() {
        return Clock.system(ZONE);
    }
}
