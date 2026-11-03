package com.messenger.backend.user

import com.messenger.backend.chat.UserSummary
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
class UserSearchController(private val users: UserRepository) {

    @GetMapping("/search")
    fun search(@AuthenticationPrincipal me: Long, @RequestParam q: String): List<UserSummary> {
        if (q.isBlank()) return emptyList()
        return users.search(q.trim(), me, PageRequest.of(0, 20))
            .map { UserSummary(it.id, it.username, it.displayName, it.avatarUrl) }
    }
}