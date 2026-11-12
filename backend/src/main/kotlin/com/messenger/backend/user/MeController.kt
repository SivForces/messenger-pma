package com.messenger.backend.user

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
class MeController(private val users: UserRepository) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal userId: Long): ProfileResponse =
        find(userId).toProfile()

    @PutMapping("/me")
    @Transactional
    fun update(
        @AuthenticationPrincipal userId: Long,
        @Valid @RequestBody req: UpdateProfileRequest
    ): ProfileResponse {
        val user = find(userId)
        user.displayName = req.displayName.trim()
        user.phone = req.phone?.trim()?.ifBlank { null }
        user.bio = req.bio?.trim()?.ifBlank { null }
        user.birthDate = req.birthDate
        return user.toProfile()
    }

    private fun find(id: Long): User = users.findById(id).orElseThrow {
        ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}