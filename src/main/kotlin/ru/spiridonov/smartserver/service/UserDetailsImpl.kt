package ru.spiridonov.smartserver.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.spiridonov.smartserver.model.User
import java.util.stream.Collectors

class UserDetailsImpl(
    private val id: Long,
    private val username: String,
    private val email: String,
    private val password: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities.toMutableList()

    override fun getPassword() = password

    fun getId() = id

    override fun getUsername() = username

    fun getEmail() = email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    companion object{
        @JvmStatic
        fun build(user: User): UserDetailsImpl {
            val authorities: List<GrantedAuthority> = user.roles?.stream()
                ?.map { role -> SimpleGrantedAuthority(role.role.name) }
                ?.collect(Collectors.toList()) ?: emptyList()
            return UserDetailsImpl(
                user.id ?: 0,
                user.userName,
                user.email,
                user.password,
                authorities
             )
        }
    }
}