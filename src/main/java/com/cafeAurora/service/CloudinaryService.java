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
    
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        
        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
        
        // Subir a Cloudinary con carpeta organizada
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
            ObjectUtils.asMap(
                "folder", "cafe-aurora/items",
                "public_id", "item_" + UUID.randomUUID(),
                "resource_type", "auto"
            )
        );
        
        return (String) uploadResult.get("secure_url");
    }
    
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Extraer public_id de la URL
                String publicId = extractPublicId(imageUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log el error pero no lanzar excepción
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
    }
    
    private String extractPublicId(String imageUrl) {
        // Ejemplo URL: https://res.cloudinary.com/demo/image/upload/v1234567890/cafe-aurora/items/item_uuid.jpg
        // Extraer: cafe-aurora/items/item_uuid
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                // Remover versión si existe (v1234567890/)
                path = path.replaceFirst("v\\d+/", "");
                // Remover extensión
                return path.substring(0, path.lastIndexOf('.'));
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo public_id: " + e.getMessage());
        }
        return imageUrl;
    }
}