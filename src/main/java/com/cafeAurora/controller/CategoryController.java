package com.cafeAurora.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafeAurora.model.Category;
import com.cafeAurora.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping("/list")
	public List<Category> listAllCategories(){
		return categoryService.getAllCategories();
	}
}
