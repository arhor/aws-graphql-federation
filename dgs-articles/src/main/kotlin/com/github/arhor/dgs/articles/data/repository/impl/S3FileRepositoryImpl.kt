package com.github.arhor.dgs.articles.data.repository.impl

import com.github.arhor.dgs.articles.config.props.AppProps
import com.github.arhor.dgs.articles.data.repository.FileRepository
import io.awspring.cloud.s3.S3Exception
import io.awspring.cloud.s3.S3Operations
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.io.InputStream
import java.util.UUID

@Component
@Retryable(retryFor = [S3Exception::class])
class S3FileRepositoryImpl(
    private val s3Operations: S3Operations,
    appProps: AppProps,
) : FileRepository {

    private val bannersBucketName: String = appProps.aws.s3.bannersBucketName

    override fun upload(filename: String, data: InputStream) {
        s3Operations.upload(bannersBucketName, filename, data)
    }

    override fun download(filename: String): InputStream {
        // FIXME: input stream must be closed correctly here
        return s3Operations.download(bannersBucketName, filename).inputStream
    }
}
