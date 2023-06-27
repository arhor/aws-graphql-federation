package com.github.arhor.dgs.articles.data.repository

import java.io.InputStream

interface FileRepository {

    fun upload(filename: String, data: InputStream)
    fun download(filename: String): InputStream
    fun delete(filename: String)
}
