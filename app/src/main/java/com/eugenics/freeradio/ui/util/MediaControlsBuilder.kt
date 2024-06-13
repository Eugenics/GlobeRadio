package com.eugenics.freeradio.ui.util

import com.eugenics.media_service.media.FreeRadioMediaServiceConnection

class MediaControlsBuilder {
    private val mediaControls: MediaControlsImpl = MediaControlsImpl.newInstance()

    fun setMediaServiceConnection(
        mediaServiceConnection: FreeRadioMediaServiceConnection
    ): MediaControlsBuilder {
        mediaControls.setMediaServiceConnection(mediaServiceConnection)
        return this
    }

    fun build(): IMediaControls = mediaControls
}