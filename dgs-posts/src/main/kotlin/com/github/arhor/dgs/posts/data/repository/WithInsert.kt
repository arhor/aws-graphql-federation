package com.github.arhor.dgs.posts.data.repository

interface WithInsert<E : Any> {

    fun insert(entity: E)
}
