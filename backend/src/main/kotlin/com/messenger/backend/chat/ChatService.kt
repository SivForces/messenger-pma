package com.messenger.backend.chat

import com.messenger.backend.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class ChatService(
    private val chats: ChatRepository,
    private val members: ChatMemberRepository,
    private val messages: MessageRepository,
    private val users: UserRepository
) {

    @Transactional
    fun createDirect(me: Long, partnerId: Long): ChatDto {
        if (partnerId == me) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "It is not possible to create chat with yourself")
        }
        if (!users.existsById(partnerId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found")
        }
        val existing = members.findDirectChatIds(me, partnerId).firstOrNull()
        val chatId = existing ?: run {
            val chat = chats.save(Chat())
            members.save(ChatMember(ChatMemberId(chat.id, me)))
            members.save(ChatMember(ChatMemberId(chat.id, partnerId)))
            chat.id
        }
        return toDto(requireMember(me, chatId), me)
    }

    @Transactional(readOnly = true)
    fun list(me: Long): List<ChatDto> =
        members.findByIdUserId(me)
            .map { toDto(it, me) }
            .sortedByDescending { it.updatedAt }

    @Transactional
    fun getMessages(me: Long, chatId: Long, before: Long?, after: Long?, limit: Int): List<MessageDto> {
        val member = requireMember(me, chatId)
        val size = limit.coerceIn(1, 100)
        val result = if (after != null) {
            messages.findByChatIdAndIdGreaterThanOrderByIdAsc(chatId, after, PageRequest.of(0, size))
        } else {
            messages.findByChatIdAndIdLessThanOrderByIdDesc(
                chatId, before ?: Long.MAX_VALUE, PageRequest.of(0, size)
            ).reversed()
        }
        member.lastReadAt = Instant.now()
        return result.map { it.toDto() }
    }

    @Transactional
    fun send(me: Long, chatId: Long, text: String): MessageDto {
        val member = requireMember(me, chatId)
        val message = messages.save(Message(chatId = chatId, senderId = me, text = text.trim()))
        member.lastReadAt = Instant.now()
        return message.toDto()
    }

    private fun requireMember(me: Long, chatId: Long): ChatMember =
        members.findById(ChatMemberId(chatId, me)).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Chat is not found")
        }

    private fun toDto(member: ChatMember, me: Long): ChatDto {
        val chatId = member.id.chatId
        val chat = chats.findById(chatId).orElseThrow()
        val partnerId = members.findByIdChatId(chatId).first { it.id.userId != me }.id.userId
        val partner = users.findById(partnerId).orElseThrow()
        val last = messages.findFirstByChatIdOrderByIdDesc(chatId)
        val unread = messages.countByChatIdAndSenderIdNotAndCreatedAtAfter(
            chatId, me, member.lastReadAt ?: Instant.EPOCH
        )
        return ChatDto(
            id = chatId,
            partner = UserSummary(partner.id, partner.username, partner.displayName, partner.avatarUrl),
            lastMessage = last?.toDto(),
            unreadCount = unread,
            mutedUntil = member.mutedUntil,
            updatedAt = last?.createdAt ?: chat.createdAt
        )
    }
}