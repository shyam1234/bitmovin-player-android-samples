package com.bitmovin.player.samples.custom.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.ScalingMode
import com.bitmovin.player.api.ui.StyleConfig

class PlayerManager(
    private val context: Context?,
    private val playerView: PlayerView?,
    private val listener: IPlayerStatus
) {
    private var handler: Handler = Handler(Looper.myLooper()!!)
    private var runnable: Runnable? = null
    private var url: String? = null
    init {
        listener.onPrepare()
    }

    fun playContent() {
        context?.let {
            initializePlayer(it)
            playMediaWithDelay(DELAY_IN_VIDEO_TRAILER_PLAYBACK)
        }
    }

    fun getContentDuration(): Long?{
        context?.let {
            return  (playerView?.player?.duration?.times(1000))?.toLong()
        }
        return null
    }

    private fun initializePlayer(context: Context) {
        // Create a new PlayerConfig containing a StyleConfig with disabled UI
        val playerConfig = PlayerConfig(styleConfig = StyleConfig(isUiEnabled = false, scalingMode = ScalingMode.Zoom))
        playerView?.player = null
        Player.create(context, playerConfig).also { playerView?.player = it }
        //load url
        if(!TextUtils.isEmpty(url)) {
            playerView?.player?.load(SourceConfig.fromUrl(url!!))
        }
        addEventListener()
    }

    private fun playMediaWithDelay(delayInMillis: Long) {
        if (runnable == null) {
            runnable = Runnable {
                if ( playerView?.player?.isPlaying == false) {
                    playerView.player?.play()
                }
            }
        }
        runnable?.let {
            handler.postDelayed(it, delayInMillis)
        }
    }

    private fun onPlayerEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Play -> {
                listener.onPlay()
            }
            is PlayerEvent.Destroy,
            is PlayerEvent.Paused,
            is PlayerEvent.PlaybackFinished -> {
                listener.onEnd()
            }
            else -> {
            }
        }

    }

    private fun addEventListener() {
         playerView?.player?.on<PlayerEvent.Error>(::onErrorEvent)
        playerView?.player?.on<SourceEvent.Error>(::onErrorEvent)
        playerView?.player?.on<PlayerEvent.Play>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.PlaybackFinished>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.Paused>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.StallEnded>(::onPlayerEvent)
    }

    private fun removePlayerListener() {
        playerView?.player?.off(::onErrorEvent)
        playerView?.player?.off(::onPlayerEvent)
    }

    private fun onErrorEvent(errorEvent: ErrorEvent) {
        listener.onEnd()
    }

    fun stopContent() {
        playerView?.player?.onPause()
        playerView?.onPause()
        playerView?.player?.onStop()
        playerView?.onStop()
        playerView?.player?.unload()
        runnable?.let { handler.removeCallbacks(it) }
        onDestroy()
    }

    private fun onDestroy() {
        removePlayerListener()
        playerView?.player?.destroy()
        playerView?.player = null
        playerView?.onDestroy()
    }

    fun setURL(url: String?) {
        if(!TextUtils.isEmpty(url)) {
            this.url = url
        }
    }

    companion object {
        private const val DELAY_IN_VIDEO_TRAILER_PLAYBACK: Long = 1000
    }
}