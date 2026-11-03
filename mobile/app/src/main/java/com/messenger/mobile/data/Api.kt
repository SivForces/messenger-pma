package com.messenger.mobile.data

import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val username: String, val displayName: String, val password: String)
data class LoginRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val userId: Long, val username: String, val displayName: String)

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse
}