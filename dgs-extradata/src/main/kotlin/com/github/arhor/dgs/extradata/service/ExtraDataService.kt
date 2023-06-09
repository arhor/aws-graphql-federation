package com.github.arhor.dgs.extradata.service

import com.github.arhor.dgs.extradata.generated.graphql.types.CreateExtraDataRequest
import com.github.arhor.dgs.extradata.generated.graphql.types.ExtendedEntityType
import com.github.arhor.dgs.extradata.generated.graphql.types.ExtraData

interface ExtraDataService {

    fun createExtraData(request: CreateExtraDataRequest): ExtraData

    fun getExtraDataInBatch(type: ExtendedEntityType, entityIds: Set<String>): Map<String, ExtraData>
}
