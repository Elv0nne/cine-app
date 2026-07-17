package com.example.cine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cine.data.MovieItem
import com.example.cine.data.fixImage
import com.example.cine.ui.MovieViewModel
import com.example.cine.ui.components.MovieCard
import com.example.cine.ui.components.ShimmerRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: MovieViewModel, nav: NavController) {
    val banner by vm.banner.collectAsState()
    val rows by vm.rows.collectAsState()
    val loading by vm.loading.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "CINE",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { nav.navigate("search") }) {
                        Icon(Icons.Default.Search, "Tìm kiếm")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { pad ->
        if (loading) {
            Column(Modifier.padding(pad).fillMaxSize()) {
                Spacer(Modifier.height(24.dp))
                repeat(3) {
                    ShimmerRow()
                    Spacer(Modifier.height(20.dp))
                }
            }
            return@Scaffold
        }

        LazyColumn(Modifier.padding(pad)) {
            item { banner?.let { b -> BannerHero(b) { nav.navigate("detail/${b.slug}") } } }
            items(rows) { row ->
                Text(
                    row.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 10.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(row.items) { m ->
                        MovieCard(m) { nav.navigate("detail/${m.slug}") }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun BannerHero(movie: MovieItem, onPlay: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(440.dp)) {
        AsyncImage(
            model = fixImage(movie.thumbUrl.ifBlank { movie.posterUrl }),
            contentDescription = movie.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.4f to Color.Transparent,
                    1f to MaterialTheme.colorScheme.background
                )
            )
        )
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(
                movie.name, color = Color.White,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(4.dp))
            Text("${movie.originName} • ${movie.year}", color = Color(0xFFBBBBBB))
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onPlay,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.PlayArrow, null)
                Spacer(Modifier.width(6.dp))
                Text("Xem ngay", fontWeight = FontWeight.Bold)
            }
        }
    }
}
