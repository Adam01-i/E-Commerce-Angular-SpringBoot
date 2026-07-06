package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import user_service.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité User.
 * Toutes les requêtes nécessaires au User/Auth Service sont couvertes par
 * les méthodes dérivées ci-dessous ; aucune requête JPQL/native n'est
 * nécessaire pour ce périmètre fonctionnel.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse email (identifiant de connexion).
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie l'existence d'un compte pour une adresse email donnée.
     * Utilisé lors de l'inscription pour empêcher les doublons d'email.
     */
    boolean existsByEmail(String email);

    /**
     * Retourne l'ensemble des utilisateurs selon leur statut actif/inactif.
     * Utilisé notamment par le tableau de bord administrateur et par les
     * fonctionnalités de gestion des comptes.
     */
    List<User> findByIsActive(Boolean isActive);
}
