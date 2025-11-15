package com.cafeAurora.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;
    
    // SUBIR IMAGEN PARA ITEMS
    public String uploadItemImage(MultipartFile file) throws IOException {
        return uploadImageToFolder(file, "cafe-aurora/items", "item_");
    }

    // SUBIR IMAGEN PARA GALERÍA
    public String uploadGalleryImage(MultipartFile file) throws IOException {
        return uploadImageToFolder(file, "cafe-aurora/gallery", "gallery_");
    }

    private String uploadImageToFolder(MultipartFile file, String folder, String prefix) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", folder,
                "public_id", prefix + UUID.randomUUID(),
                "resource_type", "auto"
            )
        );
        
        return (String) uploadResult.get("secure_url");
    }
    
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String publicId = extractPublicId(imageUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
    }
    
    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                path = path.replaceFirst("v\\d+/", "");
                return path.substring(0, path.lastIndexOf('.'));
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo public_id: " + e.getMessage());
        }
        return imageUrl;
    }
}