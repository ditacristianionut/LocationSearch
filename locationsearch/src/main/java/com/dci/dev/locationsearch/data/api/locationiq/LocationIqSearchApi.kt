package com.dci.dev.locationsearch.data.api.locationiq

import com.dci.dev.locationsearch.data.api.LocationSearchApi
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationIqSearchApi : LocationSearchApi {

    companion object {
        const val BASE_URL = "https://us1.locationiq.com"
    }

    @GET("/v1/search")
    suspend fun search(
        @Query("q", encoded = true) query: String,
        @Query("key") key: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("addressdetails") addressDetails: Int = 1
    ): List<LocationIqSearchResultDto>
}