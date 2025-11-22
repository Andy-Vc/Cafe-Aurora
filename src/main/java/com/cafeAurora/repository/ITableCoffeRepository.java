package com.cafeAurora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.TableCoffe;

@Repository
public interface ITableCoffeRepository extends JpaRepository<TableCoffe, Integer>{

}
