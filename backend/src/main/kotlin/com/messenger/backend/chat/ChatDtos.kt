package com.messenger.backend.chat

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

data class UserSummary(
    val id: Long,
    val username: String,
    val displayName: String,
    val avatarUrl: String?
)

data class MessageDto(
    val id: Long,
    val chatId: Long,
    val senderId: Long,
    val type: String,
    val text: String?,
    val createdAt: Instant
)

data class ChatDto(
    val id: Long,
    val partner: UserSummary,
    val lastMessage: MessageDto?,
    val unreadCount: Long,
    val mutedUntil: Instant?,
    val updatedAt: Instant
)

data class CreateChatRequest(
    @field:NotNull val userId: Long?
)

data class SendMessageRequest(
    @field:NotBlank @field:Size(max = 4000) val text: String
)

fun Message.toDto() = MessageDto(id, chatId, senderId, type, text, createdAt)