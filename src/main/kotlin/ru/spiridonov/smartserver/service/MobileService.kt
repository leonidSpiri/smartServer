package ru.spiridonov.smartserver.service

import org.springframework.stereotype.Service
import ru.spiridonov.smartserver.repository.MobileRepository

@Service
class MobileService(val mobileRepository: MobileRepository) {
}