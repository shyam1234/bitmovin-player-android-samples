package com.bitmovin.player.samples.custom.ui

/**
 * to handle the event based on the player playback states
 */
interface IPlayerStatus {
        fun onPrepare()
        fun onPlay()
        fun onEnd()
}