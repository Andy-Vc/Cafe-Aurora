package com.cafeAurora.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Gallery;
import com.cafeAurora.repository.IGalleryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalleryService {
	private final IGalleryRepository galleryRepository;
	private final CloudinaryService cloudinaryService;

	public List<Gallery> getAllVisibles() {
		return galleryRepository.findAllByIsVisibleTrueOrderByCreatedAtAsc();
	}
	
	public List<Gallery> getAllFeatured() {
		return galleryRepository.findAllByFeaturedTrueAndIsVisibleTrueOrderByCreatedAtAsc();
	}
	/*Crud Services*/
	public List<Gallery> getAll() {
		return galleryRepository.findAllByOrderByIdGalleryAsc();
	}

	public Gallery getOne(Integer id) {
		return galleryRepository.findById(id).orElseThrow();
	}

	public ResultResponse create(Gallery gallery, MultipartFile image) throws IOException {
		if (image != null && !image.isEmpty()) {
			String imageUrl = cloudinaryService.uploadGalleryImage(image);
			gallery.setImageUrl(imageUrl);
		}
		try {
			if (galleryRepository.findByTitle(gallery.getTitle()) != null) {
				return new ResultResponse(false,
						"El titulo de la imagen " + gallery.getTitle() + " ya existe en el sistema");
			}
			String message = "Imagen de galería registrada correctamente";
			galleryRepository.save(gallery);

			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse update(Gallery gallery, MultipartFile image) throws IOException {

		Gallery existing = galleryRepository.findById(gallery.getIdGallery()).orElseThrow();
		String oldImageUrl = existing.getImageUrl();

		if (image != null && !image.isEmpty()) {
			if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
				cloudinaryService.deleteImage(oldImageUrl);
			}

			String newImageUrl = cloudinaryService.uploadGalleryImage(image);
			gallery.setImageUrl(newImageUrl);
		} else {
			gallery.setImageUrl(oldImageUrl);
		}
		try {
			Gallery existingGallery = galleryRepository.findByTitle(gallery.getTitle());
			if (existingGallery != null && !existingGallery.getIdGallery().equals(gallery.getIdGallery())) {
				return new ResultResponse(false,
						"El titulo de la imagen " + gallery.getTitle() + " ya existe en el sistema");
			}

			String message = "Imagen de galería actualizada correctamente";
			galleryRepository.save(gallery);

			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse delete(Integer id) {
		Gallery gallery = galleryRepository.findById(id).orElseThrow();

		String action = gallery.getIsVisible() ? "ocultada" : "activada";
		gallery.setIsVisible(!gallery.getIsVisible());

		try {
			Gallery updated = galleryRepository.save(gallery);
			String message = String.format("La imagen '%s' ha sido %s correctamente",
					updated.getTitle() != null ? updated.getTitle() : ("ID " + updated.getIdGallery()),
					action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse toggleFeatured(Integer id) {
		Gallery gallery = galleryRepository.findById(id).orElseThrow();

		String action = gallery.getFeatured() ? "retirada de destacados" : "agregada a destacados";
		gallery.setFeatured(!gallery.getFeatured());

		try {
			Gallery updated = galleryRepository.save(gallery);
			String message = String.format("La imagen '%s' ha sido %s correctamente",
					updated.getTitle() != null ? updated.getTitle() : ("ID " + updated.getIdGallery()),
					action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

}
