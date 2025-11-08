package com.cafeAurora.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

}
