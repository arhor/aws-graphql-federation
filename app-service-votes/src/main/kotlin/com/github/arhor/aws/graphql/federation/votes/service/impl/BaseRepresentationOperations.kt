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

    fun <D, R, K> findRepresentationsInBatch(
        repo: CrudRepository<R, K>,
        keys: Set<K>,
        onPresent: (R) -> D,
        onMissing: (K) -> D,
    ): Map<K, D> where K : Any,
                       R : Persistable<K> {

        if (keys.isEmpty()) {
            return emptyMap()
        }
        val result = repo.findAllById(keys).associateByTo(HashMap(keys.size), { it.id!! }, onPresent)
        val idsMissingInDB = keys - result.keys

        if (idsMissingInDB.isNotEmpty()) {
            result += idsMissingInDB.associateWith(onMissing).also {
                logger.warn("Stubbed representations missing in the DB: {}", it)
            }
        }
        return result
    }


    fun <R, K> createRepresentationInDB(
        repo: CrudRepository<R, K>,
        representation: R,
    ) where K : Any,
            R : Persistable<K> {

        repo.save(representation)
    }

    fun <R, K> deleteRepresentationInDB(
        repo: CrudRepository<R, K>,
        representation: R,
    ) where K : Any,
            R : Persistable<K> {

        repo.delete(representation)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
