package ru.spiridonov.smartserver.repository

import com.example.demo.models.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import ru.spiridonov.smartserver.model.User
import java.util.*

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): Optional<RefreshToken>

    @Modifying
    fun deleteByUser(user: User)
}