package com.messenger.backend.chat

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "messages")
class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "chat_id", nullable = false)
    val chatId: Long,

    @Column(name = "sender_id", nullable = false)
    val senderId: Long,

    @Column(nullable = false)
    val type: String = "text",

    @Column(columnDefinition = "text")
    val text: String? = null,

    @Column(name = "voice_url")
    var voiceUrl: String? = null,

    var duration: Int? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now()
)