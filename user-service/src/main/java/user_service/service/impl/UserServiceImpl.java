package user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user_service.dto.ChangePasswordRequest;
import user_service.dto.UpdateProfileRequest;
import user_service.dto.UserResponse;
import user_service.entity.User;
import user_service.exception.InvalidCredentialsException;
import user_service.exception.ResourceNotFoundException;
import user_service.mapper.UserMapper;
import user_service.repository.UserRepository;
import user_service.service.UserService;

import java.util.List;

/**
 * Implémentation du service de gestion des utilisateurs.
 * Couvre à la fois les opérations réalisées par l'utilisateur sur son
 * propre compte et les opérations d'administration des comptes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = findByEmailOrThrow(email);
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmailOrThrow(email);

        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setTelephone(request.getTelephone());
        user.setAdresse(request.getAdresse());
        user.setAvatarUrl(request.getAvatarUrl());

        User updated = userRepository.save(user);
        log.info("Profil mis à jour pour l'utilisateur : {}", email);

        return userMapper.toUserResponse(updated);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmailOrThrow(email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("L'ancien mot de passe est incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Mot de passe modifié pour l'utilisateur : {}", email);
    }

    @Override
    @Transactional
    public UserResponse activateUser(Long userId) {
        User user = findByIdOrThrow(userId);
        user.setIsActive(true);
        User updated = userRepository.save(user);
        log.info("Compte activé pour l'utilisateur id={}", userId);
        return userMapper.toUserResponse(updated);
    }

    @Override
    @Transactional
    public UserResponse deactivateUser(Long userId) {
        User user = findByIdOrThrow(userId);
        user.setIsActive(false);
        User updated = userRepository.save(user);
        log.info("Compte désactivé pour l'utilisateur id={}", userId);
        return userMapper.toUserResponse(updated);
    }

    @Override
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse findUserById(Long userId) {
        User user = findByIdOrThrow(userId);
        return userMapper.toUserResponse(user);
    }

    private User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé avec l'email : " + email));
    }

    private User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé avec l'id : " + userId));
    }
}
