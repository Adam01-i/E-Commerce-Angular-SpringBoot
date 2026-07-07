package user_service.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import user_service.entity.User;
import user_service.enums.Role;
import user_service.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * Amorce un compte ADMIN au premier démarrage si aucun compte de ce rôle
 * n'existe encore. Sans ce compte, aucune fonctionnalité d'administration
 * (cahier des charges 2.1.3) n'est jamais accessible : /api/auth/register
 * n'attribue jamais que le rôle UTILISATEUR par défaut, et aucun endpoint
 * ne permet de promouvoir un compte existant.
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.admin-seed.email:admin@ecommerce.local}")
    private String adminEmail;

    @Value("${application.admin-seed.password:Admin1234!}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        boolean adminExists = userRepository.findByEmail(adminEmail).isPresent();
        if (adminExists) {
            return;
        }

        User admin = User.builder()
                .nom("Administrateur")
                .prenom("Plateforme")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .profil(Role.ADMIN)
                .isActive(true)
                .dateCreation(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        logger.info("Compte ADMIN initial créé : {} / {}", adminEmail, adminPassword);
    }
}
