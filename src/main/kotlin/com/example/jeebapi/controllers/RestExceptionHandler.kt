package com.example.jeebapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

    /**
     * Handles access denied errors (e.g., non-admin tries admin action).
     * Translates to HTTP 403 Forbidden.
     */
    @ExceptionHandler(IllegalAccessException::class)
    fun handleAccessDenied(ex: IllegalAccessException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    /**
     * Handles bad requests (e.g., email already exists).
     * Translates to HTTP 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.message)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    /**
     * Handles requests for entities that do not exist.
     * Translates to HTTP 404 Not Found.
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
}

data class ErrorResponse(val status: Int, val message: String?)