package ru.spiridonov.smartserver.security


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.spiridonov.smartserver.security.jwt.AuthEntryPointJwt
import ru.spiridonov.smartserver.security.jwt.AuthTokenFilter
import ru.spiridonov.smartserver.service.UserDetailsServiceImpl

@Configuration
@EnableMethodSecurity
class WebSecurityConfig(
    val userDetailsServiceImpl: UserDetailsServiceImpl,
    val unauthorizedHandler: AuthEntryPointJwt
) {

    @Bean
    fun authenticationJwtTokenFilter() = AuthTokenFilter()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsServiceImpl)
        authProvider.setPasswordEncoder(passwordEncoder())

        return authProvider
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration) =
        authConfig.authenticationManager ?: throw Exception(
            "Authentication manager is not configured"
        )

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .exceptionHandling { exception: ExceptionHandlingConfigurer<HttpSecurity?> ->
                exception.authenticationEntryPoint(
                    unauthorizedHandler
                )
            }
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/mobile/**").fullyAuthenticated()
                    .requestMatchers("/api/rasp_dev/**").fullyAuthenticated()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/rasp_state/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll() //TODO set to authenticated only
                    .requestMatchers("/v3/api-docs/**").permitAll() //TODO set to authenticated only
                    .anyRequest().fullyAuthenticated()
            }

        http.authenticationProvider(authenticationProvider())

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}