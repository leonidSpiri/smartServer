package ru.spiridonov.smartserver.service

import org.springframework.stereotype.Service
import ru.spiridonov.smartserver.repository.RaspStateRepository

@Service
class RaspStateService(val raspStateRepository: RaspStateRepository) {
}