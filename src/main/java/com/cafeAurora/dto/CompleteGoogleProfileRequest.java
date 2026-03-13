package com.cafeAurora.dto;

import lombok.Data;

@Data
public class CompleteGoogleProfileRequest {
	private String userId;
	private String phone;
	private String password;
}
