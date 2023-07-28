package com.dci.dev.locationsearch.data.api.locationiq

import com.dci.dev.locationsearch.domain.model.Location
import com.squareup.moshi.Json
import kotlin.random.Random

data class LocationIqSearchResultDto(
	@Json(name="place_id") val id: String?,
	val lat: String,
	val lon: String,
	@Json(name = "display_name") val displayName: String?,
	val address: Address?
) {
	fun toModel(): Location {
		return Location(
			id = this.id?.toIntOrNull() ?: Random.nextInt(),
			name = if (this.address?.asDisplayName().isNullOrBlank()) {
				this.displayName.orEmpty()
			} else {
				this.address?.asDisplayName()
			},
			region = this.address?.county ?: this.address?.municipality,
			latitude = this.lat.toDouble(),
			longitude = this.lon.toDouble(),
			country = this.address?.country ?: this.displayName.orEmpty().split(", ").lastOrNull() ?: "",
			remoteId = null
		)
	}
}

data class Address(
	val town: String?,
	val village: String?,
	val municipality: String?,
	val county: String?,
	val country: String?,
	@Json(name = "country_code") val countryCode: String?
) {
	fun asDisplayName(): String? {
		var displayName: String? = ""
		if (!town.isNullOrBlank()) {
			displayName += town

			if (!county.isNullOrBlank()) {
				displayName += ", $county"
			}

			if (!country.isNullOrBlank()) {
				displayName += ", $country"
			}
		} else if (!village.isNullOrBlank()) {
			displayName += village

			if (!municipality.isNullOrBlank()) {
				displayName += ", $municipality"
			}

			if (!county.isNullOrBlank()) {
				displayName += ", $county"
			}

			if (!country.isNullOrBlank()) {
				displayName += ", $country"
			}
		}

		return displayName
	}
}
