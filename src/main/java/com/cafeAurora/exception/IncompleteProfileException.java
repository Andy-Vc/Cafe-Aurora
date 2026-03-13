package com.cafeAurora.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IncompleteProfileException extends RuntimeException {
    private final String token;
    private final String userId;
    private final String email;
    private final String name;
}
