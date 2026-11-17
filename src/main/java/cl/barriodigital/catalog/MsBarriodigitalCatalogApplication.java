package cl.barriodigital.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QUÉ ES: punto de entrada del microservicio de catálogo (ms-catalog).
 *
 * QUÉ HACE: administra los tipos de trámite (nombre, requisitos, cupo diario, activo/inactivo) y controla
 * el cupo diario con descuento atómico. Guarda todo en Oracle. Es una API REST protegida con JWT de Azure.
 * Lo consume el frontend (vía BFF) y el microservicio requests (para descontar cupo al admitir).
 */
@SpringBootApplication
public class MsBarriodigitalCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsBarriodigitalCatalogApplication.class, args);
	}

}
