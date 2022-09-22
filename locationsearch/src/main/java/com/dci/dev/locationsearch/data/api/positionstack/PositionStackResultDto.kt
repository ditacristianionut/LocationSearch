package com.dci.dev.locationsearch.data.api.positionstack

import com.dci.dev.locationsearch.domain.model.Location
import com.squareup.moshi.Json
import kotlin.random.Random

data class PositionStackResultDto(
	@Json(name="data")
	val data: List<ResultsItem> = emptyList()
) {
	fun toModel(): List<Location> {
		return data.map { it.toModel() }
	}
}

data class ResultsItem(

	@Json(name="country")
	val country: String? = null,

	@Json(name="latitude")
	val latitude: Double,

	@Json(name="confidence")
	val confidence: Double? = 0.0,

	@Json(name="label")
	val label: String? = null,

	@Json(name="type")
	val type: String? = null,

	@Json(name="number")
	val number: String? = null,

	@Json(name="country_code")
	val countryCode: String? = null,

	@Json(name="street")
	val street: String? = null,

	@Json(name="neighbourhood")
	val neighbourhood: String? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="postal_code")
	val postalCode: String? = null,

	@Json(name="region")
	val region: String? = null,

	@Json(name="longitude")
	val longitude: Double,

	@Json(name="region_code")
	val regionCode: String? = null
) {
	fun toModel(): Location { return Location(Random.nextInt(), name, region, country, latitude, longitude, null) }
}
