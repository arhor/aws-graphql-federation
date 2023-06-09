package com.github.arhor.dgs.extradata.data.repository

import com.github.arhor.dgs.extradata.data.entity.ExtraDataEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ExtraDataRepository : CrudRepository<ExtraDataEntity, UUID> {

    fun findAllByEntityTypeAndEntityIdIn(entityType: String, entityIds: Collection<String>): List<ExtraDataEntity>
}
