package com.cafeAurora.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Role;
import com.cafeAurora.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
	
    Long countByRole_IdRole(Integer idRole);
    
    List<User> findByRole(Role role);
}
