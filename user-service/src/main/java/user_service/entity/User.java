package user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import user_service.enums.Role;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant un compte utilisateur.
 *
 * Cette entité est l'unique entité persistée par le User/Auth Service et est
 * mappée exactement sur le schéma imposé par le cahier des charges :
 * une seule table "users", sans table Address ni table Role associée.
 * Le rôle est stocké comme une simple chaîne de caractères dans la colonne
 * "profil" (mappée sur l'enum Role via @Enumerated(EnumType.STRING)).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 100, nullable = false)
    private String prenom;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "profil", length = 20, nullable = false)
    private Role profil;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
