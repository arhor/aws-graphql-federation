package com.github.arhor.dgs.articles.data.repository

import com.github.arhor.dgs.articles.data.entity.ArticleEntity
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface ArticleRepository :
    CrudRepository<ArticleEntity, Long>,
    QuerydslPredicateExecutor<ArticleEntity>,
    PagingAndSortingRepository<ArticleEntity, Long>
