package user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice User/Auth Service.
 * Ce service est volontairement autonome : il ne dépend d'aucun autre
 * microservice de la plateforme et n'utilise donc aucun client OpenFeign.
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
