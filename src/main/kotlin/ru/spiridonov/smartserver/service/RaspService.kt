package ru.spiridonov.smartserver.service

import org.springframework.stereotype.Service
import ru.spiridonov.smartserver.repository.RaspRepository

@Service
class RaspService(val raspRepository: RaspRepository) {
}