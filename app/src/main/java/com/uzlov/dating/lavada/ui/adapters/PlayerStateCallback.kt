package com.uzlov.dating.lavada.ui.adapters

import com.google.android.exoplayer2.Player
/**
 * Callback to when the [PlayerView] has fetched the duration of video
 **/
interface PlayerStateCallback {

    fun onVideoDurationRetrieved(duration: Long, player: Player)
    fun onVideoBuffering(player: Player)
    fun onStartedPlaying(player: Player)
    fun onFinishedPlaying(player: Player)
}