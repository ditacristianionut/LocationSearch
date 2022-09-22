package com.dci.dev.locationsearch.data.api.positionstack

import com.dci.dev.locationsearch.data.api.LocationSearchApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PositionStackLocationSearchApi : LocationSearchApi {

    companion object {
        const val BASE_URL = "http://api.positionstack.com"
    }

    @GET("/v1/forward")
    suspend fun search(
        @Query("query", encoded = true) query: String,
        @Query("access_key") appId: String,
        @Query("limit") limit: Int = 80,
    ): PositionStackResultDto

}