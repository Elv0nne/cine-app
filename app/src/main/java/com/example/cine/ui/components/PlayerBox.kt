package com.example.cine.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerBox(
    url: String,
    startPositionMs: Long = 0,
    onProgress: (posMs: Long, durMs: Long) -> Unit = { _, _ -> }
) {
    val ctx = LocalContext.current
    val player = remember(url) {
        ExoPlayer.Builder(ctx).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            if (startPositionMs > 0) seekTo(startPositionMs)
            playWhenReady = true
        }
    }

    LaunchedEffect(url) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            if (player.duration > 0) onProgress(player.currentPosition, player.duration)
        }
    }

    DisposableEffect(url) {
        onDispose {
            if (player.duration > 0) onProgress(player.currentPosition, player.duration)
            player.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                this.player = player
                setShowNextButton(false)
                setShowPreviousButton(false)
                setFullscreenButtonClickListener { isFull ->
                    val activity = ctx as? Activity ?: return@setFullscreenButtonClickListener
                    activity.requestedOrientation = if (isFull)
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    else
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                    val window = activity.window
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    if (isFull) controller.hide(WindowInsetsCompat.Type.systemBars())
                    else controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(230.dp)
    )
}
