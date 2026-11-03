package com.messenger.backend.chat

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant

@Embeddable
data class ChatMemberId(
    @Column(name = "chat_id") val chatId: Long = 0,
    @Column(name = "user_id") val userId: Long = 0
) : Serializable

@Entity
@Table(name = "chat_members")
class ChatMember(
    @EmbeddedId
    val id: ChatMemberId,

    @Column(name = "muted_until")
    var mutedUntil: Instant? = null,

    @Column(name = "last_read_at")
    var lastReadAt: Instant? = null
)