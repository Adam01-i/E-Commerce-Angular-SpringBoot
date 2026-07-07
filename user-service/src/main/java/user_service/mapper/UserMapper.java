package user_service.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import user_service.dto.RegisterRequest;
import user_service.dto.UserResponse;
import user_service.entity.User;

/**
 * Mapper centralisant les conversions entre l'entité User et les DTO
 * exposés par l'API. Garantit qu'aucune entité JPA n'est jamais renvoyée
 * directement dans une réponse HTTP, et qu'aucun mot de passe ne transite
 * vers l'extérieur.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité User en UserResponse, en excluant explicitement
     * le mot de passe (UserResponse ne porte de toute façon pas ce champ,
     * ModelMapper ne peut donc pas le copier).
     */
    public UserResponse toUserResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    /**
     * Convertit une requête d'inscription en entité User partielle.
     * Le mot de passe est volontairement copié tel quel ici : son
     * chiffrement BCrypt est réalisé séparément par le service, jamais
     * par le mapper, afin de garder une responsabilité unique.
     */
    public User toEntity(RegisterRequest request) {
        return modelMapper.map(request, User.class);
    }
}
