package com.cafeAurora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Gallery;
import com.cafeAurora.service.GalleryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {
	private final GalleryService galleryService;

	@GetMapping("/listVisibles")
	public List<Gallery> listAllGalleryVisibles() {
		return galleryService.getAllVisibles();
	}

	@GetMapping("/listFeatured")
	public List<Gallery> listAllGalleryFeatured() {
		return galleryService.getAllFeatured();
	}

	/* Crud Enpoints */
	@GetMapping("/list")
	public List<Gallery> listAllGallery() {
		return galleryService.getAll();
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResultResponse> createGallery(@RequestPart("gallery") Gallery gallery,
			@RequestPart(value = "image", required = false) MultipartFile image) {
		try {
			ResultResponse result = galleryService.create(gallery, image);

			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse error = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getGallery(@PathVariable("id") Integer id) {
		try {
			Gallery gallery = galleryService.getOne(id);

			if (gallery == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la imagen con ID: " + id);
			}

			return ResponseEntity.ok(gallery);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar la imagen con ID: " + id);
		}
	}

	@PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResultResponse> updateGallery(@RequestPart("gallery") Gallery gallery,
			@RequestPart(value = "image", required = false) MultipartFile image) {
		try {
			ResultResponse result = galleryService.update(gallery, image);

			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse error = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResultResponse> deleteGallery(@PathVariable("id") Integer id) {
		try {
			ResultResponse result = galleryService.delete(id);

			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse error = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@PutMapping("/feature/{id}")
	public ResponseEntity<ResultResponse> featureGallery(@PathVariable("id") Integer id) {
		try {
			ResultResponse result = galleryService.toggleFeatured(id);

			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse error = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}
}
