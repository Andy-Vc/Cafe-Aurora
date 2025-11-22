package com.cafeAurora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Gallery;

@Repository
public interface IGalleryRepository extends JpaRepository<Gallery, Integer> {
	Gallery findByTitle(String title);

	List<Gallery> findAllByOrderByIdGalleryAsc();

	List<Gallery> findAllByIsVisibleTrueOrderByCreatedAtAsc();

	List<Gallery> findAllByFeaturedTrueAndIsVisibleTrueOrderByCreatedAtAsc();
}