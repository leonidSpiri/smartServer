package ru.spiridonov.smartserver.repository

import ru.spiridonov.smartserver.model.Role
import ru.spiridonov.smartserver.model.enums.Roles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    fun findByRole(role: Roles): Role
}