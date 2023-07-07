package ru.spiridonov.smartserver.model.enums

enum class DevTypes(typeValue: String, typeName: String) {
    FAN("FAN", "вентилятор"),
    CONDITIONER("CONDITIONER", "кондиционер"),
    LIGHT("LIGHT", "свет"),
    TEMP_SENSOR("TEMP_SENSOR", "датчик температуры"),
    TEMP_HUMIDITY_SENSOR("TEMP_HUMIDITY_SENSOR", "датчик температуры и влажности"),
    SECURITY_SENSOR("SECURITY_SENSOR", "датчик движения"),
}