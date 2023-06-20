package com.eugenics.media_service.player

import android.util.Log
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks

class PlayerListener : Player.Listener {

//    override fun onTracksChanged(tracks: Tracks) {
//        super.onTracksChanged(tracks)
//        Log.d(TAG, tracks.groups.size.toString())
//        if (tracks.groups.size > 0) {
//            val trackMetaData = tracks.groups[0].getTrackFormat(0).metadata
//            Log.d(TAG, trackMetaData.toString())
//        }
//    }
//
//    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
//        val title = mediaMetadata.title
//    }

    companion object {
        const val TAG = "PLAYER_LISTENER"
    }
}