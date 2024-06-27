package ru.spiridonov.smartserver.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.spiridonov.smartserver.model.Mobile

interface MobileRepository : MongoRepository<Mobile, ObjectId> {
    fun findTopByOrderByDateTimeDesc(): Mobile?
}