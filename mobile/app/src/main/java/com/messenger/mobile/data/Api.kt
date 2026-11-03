package com.messenger.mobile.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class RegisterRequest(val username: String, val displayName: String, val password: String)
data class LoginRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val userId: Long, val username: String, val displayName: String)

data class MeResponse(val id: Long, val username: String, val displayName: String)
data class UserSummary(val id: Long, val username: String, val displayName: String, val avatarUrl: String?)
data class MessageDto(
    val id: Long, val chatId: Long, val senderId: Long,
    val type: String, val text: String?, val createdAt: String
)
data class ChatDto(
    val id: Long, val partner: UserSummary, val lastMessage: MessageDto?,
    val unreadCount: Long, val mutedUntil: String?, val updatedAt: String
)
data class CreateChatRequest(val userId: Long)
data class SendMessageRequest(val text: String)

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @GET("users/me")
    suspend fun me(): MeResponse

    @GET("users/search")
    suspend fun searchUsers(@Query("q") q: String): List<UserSummary>

    @GET("chats")
    suspend fun getChats(): List<ChatDto>

    @POST("chats")
    suspend fun createChat(@Body req: CreateChatRequest): ChatDto

    @GET("chats/{id}/messages")
    suspend fun getMessages(
        @Path("id") chatId: Long,
        @Query("after") after: Long?,
        @Query("limit") limit: Int
    ): List<MessageDto>

    @POST("chats/{id}/messages")
    suspend fun sendMessage(@Path("id") chatId: Long, @Body req: SendMessageRequest): MessageDto
}