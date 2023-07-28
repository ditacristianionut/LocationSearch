package com.dci.dev.locationsearch.data.repository.locationiq

import com.dci.dev.locationsearch.data.api.locationiq.LocationIqSearchApi
import com.dci.dev.locationsearch.domain.LocationSearchRepository
import com.dci.dev.locationsearch.domain.model.Location
import com.dci.dev.locationsearch.utils.NetworkResult
import com.dci.dev.locationsearch.utils.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LocationIqSearchRepository(
    private val api: LocationIqSearchApi,
    private val apiKey: String,
    private val dispatcher: CoroutineDispatcher
) : LocationSearchRepository {

    override suspend fun search(query: String): Flow<NetworkResult<List<Location>>> {
        return flow {
            emit(safeApiCall {
                api.search(query, apiKey).map {
                    it.toModel()
                }
            }
            )
        }.flowOn(dispatcher)
    }

}