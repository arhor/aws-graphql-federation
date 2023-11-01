package com.github.arhor.aws.graphql.federation.posts.data.repository.impl

import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.data.repository.BannerImageRepository
import io.awspring.cloud.s3.S3Exception
import io.awspring.cloud.s3.S3Operations
import org.springframework.core.io.Resource
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
@Retryable(retryFor = [S3Exception::class])
class S3BannerImageRepositoryImpl(
    private val s3Operations: S3Operations,
    appProps: AppProps,
) : BannerImageRepository {

    private val bannersBucketName: String = appProps.aws.s3.bannersBucketName

    override fun upload(filename: String, data: InputStream) {
        s3Operations.upload(bannersBucketName, filename, data)
    }

    override fun download(filename: String): Resource {
        return s3Operations.download(bannersBucketName, filename)
    }

    override fun delete(filename: String) {
        s3Operations.deleteObject(bannersBucketName, filename)
    }
}
