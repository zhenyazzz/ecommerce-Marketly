package org.com.cartservice.exception;

import org.springframework.http.HttpStatus;

// DTO для ошибок
public record ErrorResponse(String message, HttpStatus status) {}
