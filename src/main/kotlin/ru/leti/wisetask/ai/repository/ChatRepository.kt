package ru.leti.wisetask.ai.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.leti.wisetask.ai.entity.ChatEntity
import java.util.*

@Repository
interface ChatRepository : JpaRepository<ChatEntity, UUID> {
    @Query("select c from ChatEntity c left join fetch c.history where c.id = :id")
    fun findByIdWithHistory(id: UUID): Optional<ChatEntity>
}