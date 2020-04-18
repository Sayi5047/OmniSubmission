package io.hustler.omnicurisassignment.player

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import androidx.media.AudioAttributesCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class PlayerHolder(
    private val context: Context,
    private val playerView: PlayerView,
    private val playerState: PlayerState,
    private val initialUriList: ArrayList<String>


) {
    val audioFocusPlayer: ExoPlayer
    lateinit var mediaSource: MediaSource

    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributesCompat.Builder()
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .build()
        audioFocusPlayer =
            AudioFocusWrapper(
                audioAttributes,
                audioManager,
                ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector()).apply {
                    playerView.player = this
                }
            )
    }

    fun strat() {
        mediaSource = (playerState.source)?.let {
            buildUserInputDynamicPlayListMediaSource()
        }
        audioFocusPlayer.prepare(mediaSource)
        with(playerState) {
            audioFocusPlayer.playWhenReady = whenready
            audioFocusPlayer.seekTo(window, position)
        }
        Log.d(this.javaClass.simpleName, "PLAYER STARTED")


    }

    fun addToPlayList(url: String) {
        if (null != mediaSource) {
            (mediaSource as DynamicConcatenatingMediaSource).addMediaSource(
                buildExtractorMediaSource(
                    Uri.parse(url)
                )
            )
        }
    }

    private fun buildExtractorMediaSource(uri: Uri): ExtractorMediaSource {
        return ExtractorMediaSource.Factory(
            DefaultDataSourceFactory(context, "assignment")
        ).createMediaSource(uri)

    }


    private fun buildUserInputDynamicPlayListMediaSource(): MediaSource {
        return DynamicConcatenatingMediaSource().apply {
            for (uri in initialUriList) {
                addMediaSource(buildExtractorMediaSource(Uri.parse(uri)))
            }
        }

    }


    fun stop() {
        with(audioFocusPlayer) {
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenready = playWhenReady
            }
            stop()
        }

    }

    fun release() {
        audioFocusPlayer.release()
        Log.d(this.javaClass.simpleName, "Player Released")


    }

    fun changeVideo(position:Int) {
        audioFocusPlayer.seekTo(position, C.TIME_UNSET)
        audioFocusPlayer.playWhenReady = true

    }
}
