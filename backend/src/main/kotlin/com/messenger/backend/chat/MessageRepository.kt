package com.messenger.backend.chat

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface MessageRepository : JpaRepository<Message, Long> {
    fun findByChatIdAndIdLessThanOrderByIdDesc(chatId: Long, before: Long, pageable: Pageable): List<Message>
    fun findByChatIdAndIdGreaterThanOrderByIdAsc(chatId: Long, after: Long, pageable: Pageable): List<Message>
    fun findFirstByChatIdOrderByIdDesc(chatId: Long): Message?
    fun countByChatIdAndSenderIdNotAndCreatedAtAfter(chatId: Long, senderId: Long, since: Instant): Long
}