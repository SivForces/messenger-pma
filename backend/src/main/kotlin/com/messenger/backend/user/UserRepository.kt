package com.messenger.backend.user

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean

    @Query(
        """
    select u from User u
    where u.id <> :me and (lower(u.username) like lower(concat(:q, '%'))
       or lower(u.displayName) like lower(concat(:q, '%')))
    order by u.username
    """
    )
    fun search(@Param("q") q: String, @Param("me") me: Long, pageable: Pageable): List<User>
}
