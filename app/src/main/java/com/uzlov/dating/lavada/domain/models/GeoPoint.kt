package com.uzlov.dating.lavada.domain.models

data class GeoPoint(
    val address: Address?,
    val boundingbox: List<String>?,
    val display_name: String?,
    val lat: String?,
    val licence: String?,
    val lon: String?,
    val osm_id: String?,
    val osm_type: String?,
    val place_id: String?
) {
    fun getViewAddress(): String {
        var result = ""

        if (address == null) {
            result = ""
            return result
        }

        if (!address.city.isNullOrEmpty()) {
            result += address.city
        } else if (!address.state.isNullOrEmpty()) {
            result += address.state
        }

        if (!address.road.isNullOrEmpty()) {
            result += ", ${address.road}"
        }

        if (!address.house_number.isNullOrEmpty()) {
            result += ", ${address.house_number}"
        }

        return result
    }
}