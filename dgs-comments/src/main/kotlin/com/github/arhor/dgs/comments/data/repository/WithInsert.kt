package com.github.arhor.dgs.comments.data.repository

interface WithInsert<E : Any> {

    fun insert(entity: E)
}
