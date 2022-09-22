package com.dci.dev.locationsearch.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    var id: Int,
    val name: String?,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val remoteId: Int?,
    ): Parcelable {

    val coordinates: String
        get() = "$latitude,$longitude"

    val niceName: String
        get() = name?.split(",")?.firstOrNull() ?: region ?: country ?: ""
}

val MockLocation = Location(
    id = -1,
    remoteId = -1,
    name = null,
    region = "My location",
    country = null,
    latitude = 41.6,
    longitude = 2.1
)

