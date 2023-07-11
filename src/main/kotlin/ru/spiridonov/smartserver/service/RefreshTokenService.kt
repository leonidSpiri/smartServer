package ru.spiridonov.smartserver.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.spiridonov.smartserver.exception.TokenRefreshException
import ru.spiridonov.smartserver.model.RefreshToken
import ru.spiridonov.smartserver.repository.RefreshTokenRepository
import ru.spiridonov.smartserver.repository.UserRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

@Service
class RefreshTokenService(
    val refreshTokenRepository: RefreshTokenRepository,
    val userRepository: UserRepository
) {
    @Value("\${ru.spiridonov.jwtRefreshExpirationMs}")
    private val refreshTokenDurationMs = 0L

    fun findByToken(token: String) = refreshTokenRepository.findByToken(token)

    fun createRefreshToken(userId: Long): RefreshToken {
        val refreshToken = RefreshToken(
            user = userRepository.findById(userId).get(),
            expiryDate = Instant.now(Clock.system(ZoneOffset.of("+03:00"))).plusMillis(refreshTokenDurationMs),
            token = UUID.randomUUID().toString()
        )
        return refreshTokenRepository.save(refreshToken)
    }

    fun verifyExpiration(refreshToken: RefreshToken) =
        if (refreshToken.expiryDate < Instant.now(Clock.system(ZoneOffset.of("+03:00")))) {
            refreshTokenRepository.delete(refreshToken)
            throw TokenRefreshException(
                refreshToken.token,
                "Refresh token was expired. Please make a new signin request"
            )
        } else refreshToken

    @Transactional
    fun deleteByUserId(userId: Long) = refreshTokenRepository.deleteByUser(userRepository.findById(userId).get())
}