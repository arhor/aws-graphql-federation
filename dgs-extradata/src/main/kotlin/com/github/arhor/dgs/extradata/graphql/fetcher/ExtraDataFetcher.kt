package com.github.arhor.dgs.extradata.graphql.fetcher

import com.github.arhor.dgs.extradata.generated.graphql.DgsConstants
import com.github.arhor.dgs.extradata.generated.graphql.types.CreateExtraDataRequest
import com.github.arhor.dgs.extradata.generated.graphql.types.ExtraData
import com.github.arhor.dgs.extradata.generated.graphql.types.Indentifiable
import com.github.arhor.dgs.extradata.graphql.loader.ExtraDataBatchLoader
import com.github.arhor.dgs.extradata.service.ExtraDataService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.concurrent.CompletableFuture

@DgsComponent
class ExtraDataFetcher(private val extraDataService: ExtraDataService) {

    @DgsMutation
    fun createExtraData(@InputArgument request: CreateExtraDataRequest): ExtraData {
        return extraDataService.createExtraData(request)
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ExtraData)
    fun userExtraData(dfe: DgsDataFetchingEnvironment): CompletableFuture<ExtraData> {
        return dfe.loadExtraDataUsing<ExtraDataBatchLoader.ForUser>()
    }

    @DgsData(parentType = DgsConstants.POST.TYPE_NAME, field = DgsConstants.POST.ExtraData)
    fun postExtraData(dfe: DgsDataFetchingEnvironment): CompletableFuture<ExtraData> {
        return dfe.loadExtraDataUsing<ExtraDataBatchLoader.ForPost>()
    }

    @DgsData(parentType = DgsConstants.TOPIC.TYPE_NAME, field = DgsConstants.TOPIC.ExtraData)
    fun topicExtraData(dfe: DgsDataFetchingEnvironment): CompletableFuture<ExtraData> {
        return dfe.loadExtraDataUsing<ExtraDataBatchLoader.ForTopic>()
    }

    private inline fun <reified T> DgsDataFetchingEnvironment.loadExtraDataUsing(): CompletableFuture<ExtraData>
        where T : ExtraDataBatchLoader {

        val loader = getDataLoader<String, ExtraData>(T::class.java)
        val source = getSource<Indentifiable>()

        return loader.load(source.id)
    }
}
