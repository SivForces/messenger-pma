package com.messenger.backend.user

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
class MeController(private val users: UserRepository) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal userId: Long): Map<String, Any> {
        val user = users.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return mapOf("id" to user.id, "username" to user.username, "displayName" to user.displayName)
    }
}