package com.messenger.backend.auth

import com.messenger.backend.security.JwtService
import com.messenger.backend.user.User
import com.messenger.backend.user.UserRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val users: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwt: JwtService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterRequest): AuthResponse {
        val username = req.username.lowercase()
        if (users.existsByUsername(username)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Юзернейм вже зайнятий")
        }
        val user = users.save(
            User(
                username = username,
                displayName = req.displayName.trim(),
                passwordHash = encoder.encode(req.password)!!
            )
        )
        return user.toAuthResponse()
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): AuthResponse {
        val user = users.findByUsername(req.username.lowercase())
        if (user == null || !encoder.matches(req.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Невірний юзернейм або пароль")
        }
        return user.toAuthResponse()
    }

    private fun User.toAuthResponse() =
        AuthResponse(jwt.generate(id, username), id, username, displayName)
}