package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.spiridonov.smartserver.model.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUserName(username: String): User?

    fun findByEmail(email: String): User?

    fun existsByUserName(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}