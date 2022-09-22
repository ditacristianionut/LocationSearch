package com.dci.dev.locationsearch.domain

import com.dci.dev.locationsearch.domain.model.Location
import com.dci.dev.locationsearch.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

interface LocationSearchRepository {

    suspend fun search(query: String): Flow<NetworkResult<List<Location>>>

}