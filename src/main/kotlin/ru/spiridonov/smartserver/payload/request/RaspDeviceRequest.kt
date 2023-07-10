package ru.spiridonov.smartserver.payload.request

data class RaspDeviceRequest(
    val devType: String,
    val pinId: Int,
    val description: String
)