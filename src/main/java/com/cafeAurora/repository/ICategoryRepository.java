package com.cafeAurora.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Integer> {

}
