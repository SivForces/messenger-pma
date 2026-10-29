package com.messenger.backend.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.Instant

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(name = "display_name", nullable = false)
    var displayName: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    var phone: String? = null,
    var bio: String? = null,

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "show_phone") var showPhone: Boolean = false,
    @Column(name = "show_birth_date") var showBirthDate: Boolean = true,
    @Column(name = "show_bio") var showBio: Boolean = true,

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now()
)