package com.example.cine.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    var isFullscreen by remember { mutableStateOf(false) }

    val player = remember(url) {
        ExoPlayer.Builder(ctx).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            if (startPositionMs > 0) seekTo(startPositionMs)
            playWhenReady = true
        }
    }

    // 2 PlayerView riêng, dùng chung 1 ExoPlayer
    val inlineView = remember {
        PlayerView(ctx).apply {
            setShowNextButton(false)
            setShowPreviousButton(false)
            player = null
            setFullscreenButtonClickListener { isFullscreen = true }
        }
    }
    val fullscreenView = remember {
        PlayerView(ctx).apply {
            setShowNextButton(false)
            setShowPreviousButton(false)
            player = null
            setFullscreenButtonClickListener { isFullscreen = false }
        }
    }

    // Chuyển player sang đúng view đang hiển thị (không reparent view)
    LaunchedEffect(isFullscreen) {
        if (isFullscreen) {
            PlayerView.switchTargetView(player, inlineView, fullscreenView)
        } else {
            PlayerView.switchTargetView(player, fullscreenView, inlineView)
        }
    }

    // Lưu tiến độ mỗi 5 giây
    LaunchedEffect(url) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            if (player.duration > 0) onProgress(player.currentPosition, player.duration)
        }
    }

    DisposableEffect(url) {
        onDispose {
            if (player.duration > 0) onProgress(player.currentPosition, player.duration)
            inlineView.player = null
            fullscreenView.player = null
            player.release()
        }
    }

    // Xoay ngang + ẩn/hiện thanh hệ thống
    val activity = ctx as? Activity
    LaunchedEffect(isFullscreen) {
        val a = activity ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(a.window, a.window.decorView)
        if (isFullscreen) {
            a.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            a.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // Player inline luôn nằm trong layout
    AndroidView(
        factory = { inlineView },
        modifier = Modifier.fillMaxWidth().height(230.dp)
    )

    // Fullscreen: Dialog phủ toàn màn hình, chứa fullscreenView
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false }, // bấm Back thoát fullscreen
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black)) {
                AndroidView(
                    factory = { fullscreenView },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
