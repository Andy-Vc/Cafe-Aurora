package com.cafeAurora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Item;

@Repository
public interface IItemRepository extends JpaRepository<Item, Integer> {
	Item findByName(String nombre);

	List<Item> findAllByOrderByIdItemAsc();

	List<Item> findByCategoryIdCatAndIsAvailableTrue(Integer idCat);

	List<Item> findByCategoryIdCatAndIsFeaturedTrueAndIsAvailableTrueOrderByCreatedAtDesc(Integer idCat);
}
