package ru.spiridonov.smartserver.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date


@Document(collection = "mobile_requirements")
data class Mobile(
    @Id
    var id: String? = null,

    val dateTime: Date,

    val userId:Long,

    val newRequiredState: MobileRequirements
)