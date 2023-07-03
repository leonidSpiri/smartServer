package ru.spiridonov.smartserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SmartServerApplication

fun main(args: Array<String>) {
    runApplication<SmartServerApplication>(*args)
}
