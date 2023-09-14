package com.eugenics.core.model

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteStation(
    val uuid: String,
    val stationuuid: String
)

@Serializable
data class Favorites(
    val stationList: List<FavoriteStation>
) {
    companion object {
        fun newInstance(favoriteStations: List<FavoriteStation>): Favorites =
            Favorites(favoriteStations)
    }
}
