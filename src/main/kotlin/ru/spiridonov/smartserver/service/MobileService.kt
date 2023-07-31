package ru.spiridonov.smartserver.service

import org.springframework.stereotype.Service
import ru.spiridonov.smartserver.repository.MobileRepository
import ru.spiridonov.smartserver.repository.RaspDevicesRepository
import ru.spiridonov.smartserver.repository.UserRepository

@Service
class MobileService(
    val raspDevicesRepository: RaspDevicesRepository,
    val mobileRepository: MobileRepository,
    val userRepository: UserRepository
) {


}