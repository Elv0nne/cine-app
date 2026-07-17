package com.example.cine.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cine.ui.MovieViewModel
import com.example.cine.ui.components.MovieCard

@Composable
fun ExploreScreen(vm: MovieViewModel, nav: NavController) {
    val genres = listOf(
        "hanh-dong" to "Hành động", "tinh-cam" to "Tình cảm",
        "kinh-di" to "Kinh dị", "hai-huoc" to "Hài hước",
        "co-trang" to "Cổ trang", "hoat-hinh" to "Hoạt hình",
        "vien-tuong" to "Viễn tưởng", "tam-ly" to "Tâm lý"
    )
    val list by vm.explore.collectAsState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) { if (list.isEmpty()) vm.loadByGenre("hanh-dong") }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { last ->
                if (last != null && last >= list.size - 6) vm.loadMore()
            }
    }

    Column(Modifier.fillMaxSize()) {
        LazyRow(contentPadding = PaddingValues(12.dp)) {
            items(genres) { (slug, label) ->
                AssistChip(
                    onClick = { vm.loadByGenre(slug) },
                    label = { Text(label) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list) { m ->
                MovieCard(m) { nav.navigate("detail/${m.slug}") }
            }
        }
    }
}
