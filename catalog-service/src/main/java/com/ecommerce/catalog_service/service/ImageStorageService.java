package com.ecommerce.catalog_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Enregistre les images de produits envoyées en multipart sur le disque local
 * (dossier monté en volume Docker) et expose l'URL publique correspondante,
 * servie par {@link com.ecommerce.catalog_service.config.WebMvcConfig}.
 */
@Service
public class ImageStorageService {

    private static final String PUBLIC_PATH_PREFIX = "/api/produits/images/";

    private final Path uploadDir;

    public ImageStorageService(@Value("${app.upload-dir:uploads/produits}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le dossier de stockage des images", e);
        }
    }

    public String store(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (StringUtils.hasText(extension) ? "." + extension : "");
        Path target = uploadDir.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Échec de l'enregistrement de l'image", e);
        }
        return PUBLIC_PATH_PREFIX + filename;
    }

    public Path getUploadDir() {
        return uploadDir;
    }
}
