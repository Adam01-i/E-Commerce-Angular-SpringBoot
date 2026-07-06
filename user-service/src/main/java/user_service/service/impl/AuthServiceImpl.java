package user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user_service.dto.*;
import user_service.entity.User;
import user_service.enums.Role;
import user_service.exception.BusinessException;
import user_service.exception.DuplicateResourceException;
import user_service.exception.InvalidCredentialsException;
import user_service.exception.UnauthorizedException;
import user_service.mapper.UserMapper;
import user_service.repository.UserRepository;
import user_service.security.JwtService;
import user_service.service.AuthService;
import user_service.util.LoginAttemptService;

/**
 * Implémentation du service d'authentification.
 * Concentre toute la logique métier liée à l'inscription, la connexion et
 * le rafraîchissement de jeton ; le contrôleur AuthController ne fait que
 * déléguer les appels à cette classe.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final LoginAttemptService loginAttemptService;

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpirationMillis;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Un compte existe déjà avec cet email : " + request.getEmail());
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .avatarUrl(request.getAvatarUrl())
                .profil(Role.UTILISATEUR)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Nouveau compte créé pour l'email : {}", savedUser.getEmail());

        return buildJwtResponse(savedUser);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        String email = request.getEmail();

        if (loginAttemptService.isLocked(email)) {
            throw new BusinessException(
                    "Compte temporairement verrouillé suite à plusieurs échecs de connexion. Veuillez réessayer plus tard.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (DisabledException ex) {
            throw new UnauthorizedException("Ce compte est désactivé. Veuillez contacter un administrateur.");
        } catch (BadCredentialsException ex) {
            loginAttemptService.registerFailedAttempt(email);
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }

        loginAttemptService.resetAttempts(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Email ou mot de passe incorrect"));

        log.info("Connexion réussie pour l'email : {}", email);
        return buildJwtResponse(user);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtService.isRefreshTokenValid(token)) {
            throw new UnauthorizedException("Le refresh token est invalide ou expiré");
        }

        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable pour ce jeton"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Ce compte est désactivé");
        }

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getProfil().name());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMillis / 1000)
                .build();
    }

    private JwtResponse buildJwtResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getProfil().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMillis / 1000)
                .user(userMapper.toUserResponse(user))
                .build();
    }
}
