package com.github.arhor.dgs.articles.data.repository

import org.springframework.core.io.Resource
import java.io.InputStream

interface BannerImageRepository {

    fun upload(filename: String, data: InputStream)
    fun download(filename: String): Resource
    fun delete(filename: String)
}
