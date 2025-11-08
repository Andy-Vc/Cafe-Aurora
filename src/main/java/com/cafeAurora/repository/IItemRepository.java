package com.cafeAurora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Item;

@Repository
public interface IItemRepository extends JpaRepository<Item, Integer>{

}
