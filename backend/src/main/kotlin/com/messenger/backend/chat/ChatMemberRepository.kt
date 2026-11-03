package com.messenger.backend.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChatMemberRepository : JpaRepository<ChatMember, ChatMemberId> {
    fun findByIdUserId(userId: Long): List<ChatMember>
    fun findByIdChatId(chatId: Long): List<ChatMember>

    @Query(
        """
        select m1.id.chatId from ChatMember m1, ChatMember m2, Chat c
        where m1.id.chatId = m2.id.chatId and c.id = m1.id.chatId
          and c.type = 'direct' and m1.id.userId = :a and m2.id.userId = :b
        """
    )
    fun findDirectChatIds(@Param("a") a: Long, @Param("b") b: Long): List<Long>
}