package com.messenger.backend.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:Size(min = 3, max = 32)
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Only letters, numbers and _")
    val username: String,

    @field:NotBlank @field:Size(max = 64)
    val displayName: String,

    @field:Size(min = 6, max = 72)
    val password: String
)

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val displayName: String
)