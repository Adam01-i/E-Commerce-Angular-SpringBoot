package user_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import user_service.entity.User;
import user_service.repository.UserRepository;

/**
 * Implémentation Spring Security de UserDetailsService, chargeant un
 * utilisateur par son email (identifiant de connexion) depuis la base
 * userdb, et l'exposant sous la forme d'un UserPrincipal.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email));
        return new UserPrincipal(user);
    }
}
