package com.cafeAurora.service;

import org.springframework.stereotype.Service;

import com.cafeAurora.repository.IUserRepository;

@Service
public class UserService {
	private final IUserRepository userRepository;

	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
}
