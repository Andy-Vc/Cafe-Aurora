package com.cafeAurora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Category;
import com.cafeAurora.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping("/listActives")
	public List<Category> listAllCategoriesActives() {
		return categoryService.getAllCategoriesActive();
	}

	/* Crud Controller */
	@GetMapping("/list")
	public List<Category> listAllCategories() {
		return categoryService.getAllCategories();
	}

	@PostMapping("/register")
	public ResponseEntity<ResultResponse> createCategory(@RequestBody Category category) {
		try {
			ResultResponse result = categoryService.createCategory(category);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
		try {
			Category getCategory = categoryService.getOne(id);
			if (getCategory == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No se encontró ninguna categoria con ID: " + id);
			}
			return ResponseEntity.ok(getCategory);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener categoria con ID: " + id);
		}
	}

	@PatchMapping("/update")
	public ResponseEntity<ResultResponse> updateCategory(@RequestBody Category category) {
		try {
			ResultResponse result = categoryService.updateCategory(category);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResultResponse> deleteCategory(@PathVariable("id") Integer id) {
		try {
			ResultResponse result = categoryService.deleteCategory(id);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
