package com.cafeAurora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByNameRole(String nameRole);
}
