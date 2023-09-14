package com.eugenics.freeradio.util

import com.eugenics.core.model.Station

object PlaylistHelper {
    fun convertStationsToPlaylist(stations: List<Station>): String {
        val stationPlaylist = StringBuilder()
        stationPlaylist.append("#EXTM3U\n")
        stations.forEach { station ->
            stationPlaylist.append("#EXTINF:-1,${station.name}\n")
            stationPlaylist.append("${station.urlResolved}\n")
        }
        return stationPlaylist.toString()
    }
}