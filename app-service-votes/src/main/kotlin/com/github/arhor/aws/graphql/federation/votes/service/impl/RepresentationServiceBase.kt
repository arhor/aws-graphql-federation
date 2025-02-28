package com.github.arhor.aws.graphql.federation.votes.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Persistable
import org.springframework.data.repository.CrudRepository

abstract class RepresentationServiceBase<K, D, E, R>(private val repository: R)
    where K : Any,
          E : Persistable<K>,
          R : CrudRepository<E, K> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    protected abstract fun construct(k: K): D
    protected abstract fun construct(r: E): D
    protected abstract fun constructRepresentation(id: K): E

    protected fun findRepresentationsInBatch(ids: Set<K>): Map<K, D> {
        // identifier
        // entity
        if (ids.isEmpty()) {
            return emptyMap()
        }
        val result =
            repository
                .findAllById(ids)
                .associateByTo(HashMap(ids.size), { it.id!! }, ::construct)

        val idsMissingInDB = ids - result.keys
        if (idsMissingInDB.isNotEmpty()) {
            for (id in idsMissingInDB) {
                result[id] = construct(id)
            }
            logger.warn("Stubbed representations missing in the DB: {}", idsMissingInDB)
        }
        return result
    }

    context(R)
    protected fun createRepresentation(id: K) {
        repository.save(constructRepresentation(id))
    }

    protected fun deleteRepresentation(id: K) {
        repository.delete(constructRepresentation(id))
    }
}
