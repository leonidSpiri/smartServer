package ru.spiridonov.smartserver.model

data class MobileRequirements(
    val fan: Boolean,
    val conditioner: Boolean,
    val tempSensor: Int,
    val boxTempSensor: Int
)