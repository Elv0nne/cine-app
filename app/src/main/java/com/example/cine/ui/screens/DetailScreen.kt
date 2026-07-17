package com.example.cine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy. LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons. Icons
import androidx.compose.material.icons.filled. Favorite
import androidx.compose.material.icons.filled. FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui. Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui.graphics. Brush
import androidx.compose.ui.graphics. Color
import androidx.compose.ui.layout. ContentScale
import androidx.compose.ui.platform. LocalContext
import androidx.compose.ui.text.font. FontWeight
import androidx.compose.ui.unit.dp
import coil.compose. AsyncImage
import com.example.cine.data. AppDatabase
import com.example.cine.data. FavoriteEntity
import com.example.cine.data. HistoryEntity
import com.example.cine.data.fixImage
import com.example.cine.ui. MovieViewModel
import com.example.cine.ui.components. PlayerBox
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(vm: MovieViewModel, slug: String) {
    LaunchedEffect(slug) { vm.loadDetail(slug) }
    val detail by vm.detail.collectAsState()

    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).dao() }
    val scope = rememberCoroutineScope()
    val isFav by dao.isFavorite(slug).collectAsState(initial = false)

    var currentUrl by remember { mutableStateOf<String?>(null) }
    var currentEp by remember { mutableStateOf<String?>(null) }
    var resumePos by remember { mutableStateOf(0L) }

    LaunchedEffect(slug) {
        dao.historyFor(slug)?.let { resumePos = it.positionMs }
    }

    val d = detail ?: run {
        Box(Modifier.fillMaxSize(), Alignment. Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        item {
            if (currentUrl != null) {
                PlayerBox(
                    url = currentUrl!!,
                    startPositionMs = resumePos,
                    onProgress = { pos, dur ->
                        scope.launch {
                            dao.upsertHistory(
                                HistoryEntity(
                                    slug = slug,
                                    name = d.movie.name,
                                    posterUrl = fixImage(d.movie.posterUrl),
                                    episodeName = currentEp ?: "",
                                    episodeUrl = currentUrl ?: "",
                                    positionMs = pos,
                                    durationMs = dur
                                )
                            )
                        }
                    }
                )
            } else {
                Box(Modifier.fillMaxWidth().height(240.dp)) {
                    AsyncImage(
                        model = fixImage(d.movie.thumbUrl.ifBlank { d.movie.posterUrl }),
                        contentDescription = d.movie.name,
                        contentScale = ContentScale. Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                0.5f to Color. Transparent,
                                1f to MaterialTheme.colorScheme.background
                            )
                        )
                    )
                }
            }
        }

        item {
            Column(Modifier.padding(16.dp)) {
                Text(
                    d.movie.name, fontWeight = FontWeight. Black,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "${d.movie.originName} • ${d.movie.year} • ${d.movie.time}",
                    color = Color(0xFFAAAAAA),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment. CenterVertically) {
                    Button(
                        onClick = {
                            scope.launch {
                                if (isFav) dao.removeFavorite(slug)
                                else dao.addFavorite(
                                    FavoriteEntity(
                                        slug, d.movie.name,
                                        fixImage(d.movie.posterUrl), d.movie.originName
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFav)
                                MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            if (isFav) Icons. Default. Favorite
                            else Icons. Default. FavoriteBorder, null
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (isFav) "Đã lưu" else "Yêu thích")
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    d.movie.content.ifBlank { "Đang cập nhật nội dung..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDDDDDD)
                )
                Spacer(Modifier.height(20.dp))
            }
        }

        d.episodes.forEach { server ->
            item {
                Text(
                    server.serverName, fontWeight = FontWeight. Bold,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Row(
                    Modifier.horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    server.serverData.forEach { ep ->
                        val selected = currentEp == ep.name && currentUrl == ep.linkM3u8
                        FilterChip(
                            selected = selected,
                            onClick = {
                                resumePos = 0
                                currentUrl = ep.linkM3u8
                                currentEp = ep.name
                            },
                            label = { Text(ep.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
