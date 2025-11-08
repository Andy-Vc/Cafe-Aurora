package com.cafeAurora.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID idUser;
    private String email;
    private String name;
    private String phone;
    private String role;
    private Boolean isActive;
}
