package com.github.arhor.dgs.extradata.data.entity

import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ExtraDataEntityIdGenerator : BeforeConvertCallback<ExtraDataEntity> {

    override fun onBeforeConvert(entity: ExtraDataEntity) = when (entity.id) {
        null -> entity.copy(id = UUID.randomUUID())
        else -> entity
    }
}
