package com.github.arhor.aws.graphql.federation.votes.service.impl

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Persistable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

/**
 * Why did I do all this unnecessary stuff with contexts,
 * making behaviour more implicit, and code more complex?
 *
 * Because I can, of course 😎
 */
@Component
class BaseRepresentationOperations {

    context(CrudRepository<R, K>)
    fun <D, R, K> findRepresentationsInBatch(keys: Set<K>, onPresent: (R) -> D, onMissing: (K) -> D): Map<K, D>
        where K : Any,
              R : Persistable<K> {

        if (keys.isEmpty()) {
            return emptyMap()
        }
        val result = findAllById(keys).associateByTo(HashMap(keys.size), { it.id!! }, onPresent)

        val idsMissingInDB = keys - result.keys
        if (idsMissingInDB.isNotEmpty()) {
            for (id in idsMissingInDB) {
                result[id] = onMissing(id)
            }
            logger.warn("Stubbed representations missing in the DB: {}", idsMissingInDB)
        }
        return result
    }


    context(CrudRepository<R, K>)
    fun <R, K> createRepresentationInDB(representation: R)
        where K : Any,
              R : Persistable<K> {

        save(representation)
    }

    context(CrudRepository<R, K>)
    fun <R, K> deleteRepresentationInDB(representation: R)
        where K : Any,
              R : Persistable<K> {

        delete(representation)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
