package ru.spiridonov.smartserver.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.spiridonov.smartserver.repository.UserRepository

@Service
class UserDetailsServiceImpl(val userRepository: UserRepository): UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUserName(username) ?: throw UsernameNotFoundException("User not found")
        return UserDetailsImpl.build(user)
    }
}