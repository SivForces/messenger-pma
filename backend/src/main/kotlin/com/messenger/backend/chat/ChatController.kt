package com.messenger.backend.chat

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chats")
@SecurityRequirement(name = "bearerAuth")
class ChatController(private val service: ChatService) {

    @PostMapping
    fun create(@AuthenticationPrincipal me: Long, @Valid @RequestBody req: CreateChatRequest) =
        service.createDirect(me, req.userId!!)

    @GetMapping
    fun list(@AuthenticationPrincipal me: Long) = service.list(me)

    @GetMapping("/{chatId}/messages")
    fun messages(
        @AuthenticationPrincipal me: Long,
        @PathVariable chatId: Long,
        @RequestParam(required = false) before: Long?,
        @RequestParam(required = false) after: Long?,
        @RequestParam(defaultValue = "50") limit: Int
    ) = service.getMessages(me, chatId, before, after, limit)

    @PostMapping("/{chatId}/messages")
    fun send(
        @AuthenticationPrincipal me: Long,
        @PathVariable chatId: Long,
        @Valid @RequestBody req: SendMessageRequest
    ) = service.send(me, chatId, req.text)
}