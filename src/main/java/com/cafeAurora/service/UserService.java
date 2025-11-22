package com.cafeAurora.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cafeAurora.model.User;
import com.cafeAurora.repository.IUserRepository;

@Service
public class UserService {
	private final IUserRepository userRepository;

	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getOne(UUID id) {
		return userRepository.findById(id).orElseThrow();
	}

}
