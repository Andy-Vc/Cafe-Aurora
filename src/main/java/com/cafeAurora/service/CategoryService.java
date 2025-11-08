package com.cafeAurora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cafeAurora.model.Category;
import com.cafeAurora.repository.ICategoryRepository;

@Service
public class CategoryService {
	private final ICategoryRepository categoryRepository;

	public CategoryService(ICategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<Category> getAllCategories(){
		return categoryRepository.findAll();
	}
}
