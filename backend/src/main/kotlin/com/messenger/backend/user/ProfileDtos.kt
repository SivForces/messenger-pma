package com.messenger.backend.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class ProfileResponse(
    val id: Long,
    val username: String,
    val displayName: String,
    val phone: String?,
    val bio: String?,
    val birthDate: LocalDate?,
    val avatarUrl: String?,
    val showPhone: Boolean,
    val showBirthDate: Boolean,
    val showBio: Boolean
)

data class UpdateProfileRequest(
    @field:NotBlank @field:Size(max = 64) val displayName: String,
    @field:Size(max = 20) val phone: String?,
    @field:Size(max = 300) val bio: String?,
    val birthDate: LocalDate?
)

fun User.toProfile() = ProfileResponse(
    id, username, displayName, phone, bio, birthDate, avatarUrl,
    showPhone, showBirthDate, showBio
)