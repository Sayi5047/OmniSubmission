package io.hustler.omnicurisassignment.ui.main

import Videos
import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.util.Rational
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import io.hustler.omnicurisassignment.BuildConfig
import io.hustler.omnicurisassignment.R
import io.hustler.omnicurisassignment.data.model.repository.getData
import io.hustler.omnicurisassignment.player.PlayerHolder
import io.hustler.omnicurisassignment.player.PlayerState
import io.hustler.omnicurisassignment.player.SourceType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_playback_controls.*


class MainActivity : AppCompatActivity(), View.OnClickListener, Player.EventListener {
    /*Player and states*/
    lateinit var playerHolder: PlayerHolder
    private val state = PlayerState()

    /*Data Objects*/
    private val uriList = ArrayList<String>()
    private var intiaVal = 0

    /*media session handles all external messages from OS */
    lateinit var mediaSession: MediaSessionCompat
    lateinit var mediaSessionConnector: MediaSessionConnector

    /*FLAG for screen config changes for manual rotation*/
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchData()
        preparePlayer()
        initiateListeners()
        buildPlayList()
    }

    private fun buildPlayList() {
        playlist_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        playlist_rv.adapter = PlayListAdapter(object : PlayListAdapter.ItemClickListener {
            override fun onClick(position: Int, videoItem: Videos) {
                playerHolder.changeVideo(position)
                video_title.text = videoItem.title
                video_subtitle.text = videoItem.subtitle

            }

        })
    }

    private fun initiateListeners() {
        addVideoToButton.setOnClickListener(this)
        exo_fullscreen_button.setOnClickListener(this)
        playerHolder.audioFocusPlayer.addListener(this)
    }

    private fun preparePlayer() {
        state.source = SourceType.playlist
        playerHolder =
            PlayerHolder(
                this.applicationContext,
                exoplayerview_activity_video,
                state,
                uriList
            )
        mediaSession = MediaSessionCompat(this, packageName)
        mediaSessionConnector = MediaSessionConnector(mediaSession)
    }

    private fun fetchData() {
        for (vid in getData().categories!![0].videos) {
            uriList.add(vid.sources[0])
        }
        val videItem: Videos = getData().categories!![0].videos[0]
        video_title.text = videItem.title
        video_subtitle.text = videItem.subtitle

    }

    override fun onStart() {
        super.onStart()
        playerHolder.strat()
        mediaSessionConnector.setPlayer(playerHolder.audioFocusPlayer, null)
        mediaSession.isActive = true
    }

    override fun onStop() {
        super.onStop()
        playerHolder.stop()
        mediaSessionConnector.setPlayer(null, null)
        mediaSession.isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHolder.release()
        mediaSession.release()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.exo_fullscreen_button -> {
                if (isFullScreen) {
                    changeScreentoPortrait()
                } else {
                    changeScreenToLandScape()


                }

            }

        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun changeScreenToLandScape() {
        exo_fullscreen_icon.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_fullscreen_exit_black_24dp
            )
        )
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        val params: ConstraintLayout.LayoutParams =
            exoplayerview_activity_video.layoutParams as ConstraintLayout.LayoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        exoplayerview_activity_video.layoutParams = params
        isFullScreen = true
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun changeScreentoPortrait() {
        exo_fullscreen_icon.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.exo_controls_fullscreen_enter
            )
        )
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        val params: ConstraintLayout.LayoutParams =
            exoplayerview_activity_video.layoutParams as ConstraintLayout.LayoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height =
            (256 * applicationContext.resources.displayMetrics.density).toInt()
        exoplayerview_activity_video.layoutParams = params
        isFullScreen = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {

    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        if (error != null) {
            if (BuildConfig.DEBUG) {
                Log.d(this.localClassName, "onError Occured " + error.message)
            }
        }

    }

    override fun onLoadingChanged(isLoading: Boolean) {
        if (BuildConfig.DEBUG) {
            Log.d(this.localClassName, "Loading Changes to $isLoading")

        }
    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(this.localClassName, "Player state change $playbackState")
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            exo_artwork_loader.visibility = View.VISIBLE;
        } else {
            exo_artwork_loader.visibility = View.INVISIBLE;
        }
        when (playbackState) {
            Player.DISCONTINUITY_REASON_PERIOD_TRANSITION -> if (BuildConfig.DEBUG) {
                Log.d(
                    this.javaClass.simpleName,
                    "eventListen onPlayerStateChanged DISCONTINUITY_REASON_PERIOD_TRANSITION"
                )
            }
            Player.STATE_BUFFERING -> if (BuildConfig.DEBUG) {
                Log.d(this.javaClass.simpleName, "Buffering . . . .")
            }
            Player.STATE_READY -> if (BuildConfig.DEBUG) {
                Log.d(this.javaClass.simpleName, "Ready!!!")
            }
            Player.STATE_ENDED -> {
                if (BuildConfig.DEBUG) {
                    Log.d(this.javaClass.simpleName, "player ended")
                }
            }
        }

    }

    override fun onUserLeaveHint() {
        val aspectRatio =
            Rational(exoplayerview_activity_video.width, exoplayerview_activity_video.height)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pictureInPictureParamsBuilder: PictureInPictureParams.Builder =
                PictureInPictureParams.Builder()
            pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build()
            enterPictureInPictureMode(pictureInPictureParamsBuilder.build())

        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        exoplayerview_activity_video.useController = !isInPictureInPictureMode
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newOrientation = newConfig.orientation

        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeScreenToLandScape()
        } else {
            changeScreentoPortrait()
        }
    }
}
