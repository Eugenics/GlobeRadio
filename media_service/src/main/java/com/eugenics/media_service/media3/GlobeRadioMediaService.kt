package com.eugenics.media_service.media3

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.eugenics.media_service.R
import com.eugenics.media_service.media3.data.ContentDataSource
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import javax.inject.Inject

private const val NOTIFICATION_LARGE_ICON_SIZE = 250 // px

@AndroidEntryPoint
@UnstableApi
class GlobeRadioMediaService : MediaLibraryService() {

    @Inject
    lateinit var contentDataSource: ContentDataSource

    private val httpDataSourceFactory =
        DefaultHttpDataSource.Factory().setDefaultRequestProperties(mapOf("1" to "Icy-MetaData"))

    private val playerAudioAttributes = AudioAttributes.Builder()
        .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(USAGE_MEDIA)
        .build()

    private var player: ExoPlayer? = null
    private var mediaLibrarySession: MediaLibrarySession? = null

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val glideOptions = RequestOptions()
        .fallback(R.drawable.pradio_wave)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .timeout(15000)

    override fun onCreate() {
        Log.d(TAG, "ON_CREATE...")
        setMediaNotificationProvider(CustomNotificationProvider(this))
        super.onCreate()
        initialize()
    }

    override fun onDestroy() {
        player?.release()
        mediaLibrarySession?.release()
        clearListener()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaLibrarySession?.player?.let { mediaPlayer ->
            if (!mediaPlayer.playWhenReady || mediaPlayer.mediaItemCount == 0) {
                stopSelf()
            }
        }
    }

    private fun initialize() {
            val exoPlayer = ExoPlayer.Builder(this)
                .setAudioAttributes(playerAudioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build()
        player = exoPlayer
        mediaLibrarySession = MediaLibrarySession
            .Builder(this, exoPlayer,initializeMediaSessionCallbacks())
            .setBitmapLoader(bitmapLoader)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    private fun initializeMediaSessionCallbacks(): MediaLibrarySession.Callback =
        GlobeRadioMediaSessionCallback(contentDataSource = contentDataSource)

    private val bitmapLoader = object : BitmapLoader {
        /** Returns whether the given `mimeType` is supported.  */
        override fun supportsMimeType(mimeType: String): Boolean = true

        /** Decodes an image from compressed binary data.  */
        override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> =
            Futures.immediateFuture(BitmapFactory.decodeByteArray(data, 0, data.size))

        /** Loads an image from `uri`.  */
        override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> =
            Futures.immediateFuture(
                serviceScope.future {
                    resolveUriAsBitmap(uri)
                }.get()
            )

    }

    private fun resolveUriAsBitmap(uri: Uri): Bitmap {
        var bitmap = ContextCompat.getDrawable(
            this@GlobeRadioMediaService,
            R.drawable.pradio_wave
        )?.toBitmap() ?: Bitmap.createBitmap(
            NOTIFICATION_LARGE_ICON_SIZE,
            NOTIFICATION_LARGE_ICON_SIZE,
            Bitmap.Config.ARGB_8888
        )
        try {
            bitmap = Glide.with(this@GlobeRadioMediaService)
                .applyDefaultRequestOptions(glideOptions)
                .asBitmap()
                .load(uri)
                .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                .get()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return bitmap
    }

    private inner class CustomNotificationProvider(context: Context) :
        DefaultMediaNotificationProvider(context) {

        override fun getNotificationContentTitle(metadata: MediaMetadata): CharSequence? {
            return metadata.title
        }

        override fun getNotificationContentText(metadata: MediaMetadata): CharSequence? {
            return metadata.station
        }
    }

    private inner class CustomLoadErrorPolicy : LoadErrorHandlingPolicy {
        /**
         * Returns whether a loader should fall back to using another resource on encountering an error,
         * and if so the duration for which the failing resource should be excluded.
         *
         *
         * If the returned [fallback type][FallbackSelection.type] was not [ ][FallbackOptions.isFallbackAvailable], then the loader will not
         * fall back.
         *
         * @param fallbackOptions The available fallback options.
         * @param loadErrorInfo A [LoadErrorInfo] holding information about the load error.
         * @return The selected fallback, or `null` if the calling loader should not fall back.
         */
        override fun getFallbackSelectionFor(
            fallbackOptions: LoadErrorHandlingPolicy.FallbackOptions,
            loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo
        ): LoadErrorHandlingPolicy.FallbackSelection? {
            TODO("Not yet implemented")
        }

        /**
         * Returns whether a loader can retry on encountering an error, and if so the duration to wait
         * before retrying. A return value of [C.TIME_UNSET] indicates that the error is fatal and
         * should not be retried.
         *
         *
         * For loads that can be retried, loaders may ignore the retry delay returned by this method in
         * order to wait for a specific event before retrying.
         *
         * @param loadErrorInfo A [LoadErrorInfo] holding information about the load error.
         * @return The duration to wait before retrying in milliseconds, or [C.TIME_UNSET] if the
         * error is fatal and should not be retried.
         */
        override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
            TODO("Not yet implemented")
        }

        /**
         * Returns the minimum number of times to retry a load before a load error that can be retried may
         * be considered fatal.
         *
         * @param dataType One of the [C.DATA_TYPE_*][C] constants indicating the type of data being
         * loaded.
         * @return The minimum number of times to retry a load before a load error that can be retried may
         * be considered fatal.
         * @see Loader.startLoading
         */
        override fun getMinimumLoadableRetryCount(dataType: Int): Int {
            TODO("Not yet implemented")
        }

    }

    companion object {
        private const val TAG = "GLOBAL_RADIO_MEDIA_SERVICE"
    }
}