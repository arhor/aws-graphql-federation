package com.github.arhor.dgs.comments.data.repository.impl

import com.github.arhor.dgs.comments.data.repository.WithInsert
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Repository

@Repository
class WithInsertImpl<E : Any>(private val template: JdbcAggregateTemplate) : WithInsert<E> {

    override fun insert(entity: E) {
        template.insert(entity)
    }
}
