package com.eugenics.freeradio.ui.util

import com.eugenics.media_service.media.FreeRadioMediaServiceConnection

class MediaControlsImpl() : IMediaControls {
    private var mediaServiceConnection: FreeRadioMediaServiceConnection? = null

    fun setMediaServiceConnection(mediaServiceConnection: FreeRadioMediaServiceConnection) {
        this.mediaServiceConnection = mediaServiceConnection
    }

    override fun play() {
        mediaServiceConnection?.transportControls?.play()
    }

    override fun play(mediaId: String) {
        mediaServiceConnection?.transportControls?.playFromMediaId(mediaId, null)
    }

    override fun search(query: String) {
        mediaServiceConnection?.transportControls?.playFromSearch(query, null)
    }

    override fun stop() {
        mediaServiceConnection?.transportControls?.stop()
    }

    override fun prev() {
        mediaServiceConnection?.transportControls?.skipToPrevious()
    }

    override fun next() {
        mediaServiceConnection?.transportControls?.skipToNext()
    }

    override fun pause() {
        mediaServiceConnection?.transportControls?.pause()
    }

    companion object {
        fun newInstance() = MediaControlsImpl()
    }
}