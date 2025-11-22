package com.cafeAurora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Category;
import com.cafeAurora.repository.ICategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final ICategoryRepository categoryRepository;

	public List<Category> getAllCategoriesActive() {
		return categoryRepository.findAllByIsActiveTrue();
	}

	/* Crud Service */
	public List<Category> getAllCategories() {
		return categoryRepository.findAllByOrderByIdCatAsc();
	}

	public Category getOne(Integer id) {
		return categoryRepository.findById(id).orElseThrow();
	}

	public ResultResponse createCategory(Category category) {
		try {
			if (categoryRepository.findByNameCat(category.getNameCat()) != null) {
				return new ResultResponse(false,
						"El nombre de la categoria " + category.getNameCat() + " ya existe en el sistema");
			}
			String message = "Categoria " + category.getNameCat() + " registrado correctamente";
			categoryRepository.save(category);
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse updateCategory(Category category) {
		try {
			Category existingCategory = categoryRepository.findByNameCat(category.getNameCat());
			if (existingCategory != null && !existingCategory.getIdCat().equals(category.getIdCat())) {
				return new ResultResponse(false,
						"El nombre de la categoria " + category.getNameCat() + " ya existe en el sistema");
			}

			String message = "Categoria " + category.getNameCat() + " actualizada correctamente";
			categoryRepository.save(category);
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse deleteCategory(Integer id) {
		Category category = categoryRepository.findById(id).orElseThrow();
		String action = category.getIsActive() ? "Desactivado" : "Activado";
		category.setIsActive(!category.getIsActive());

		try {
			Category deleted = categoryRepository.save(category);
			String message = String.format("%s ha sido %s correctamente", deleted.getNameCat(), action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}
}
