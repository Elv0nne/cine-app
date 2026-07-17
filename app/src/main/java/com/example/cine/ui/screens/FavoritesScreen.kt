package com.example.cine.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid. GridCells
import androidx.compose.foundation.lazy.grid. LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3. Text
import androidx.compose.runtime.*
import androidx.compose.ui. Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui.platform. LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation. NavController
import com.example.cine.data. AppDatabase
import com.example.cine.data. MovieItem
import com.example.cine.ui.components. MovieCard

@Composable
fun FavoritesScreen(nav: NavController) {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).dao() }
    val favs by dao.favorites().collectAsState(initial = emptyList())

    if (favs.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment. Center) {
            Text("Chưa có phim yêu thích")
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells. Fixed(3),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(favs) { f ->
            MovieCard(
                MovieItem(
                    name = f.name,
                    slug = f.slug,
                    originName = f.originName,
                    posterUrl = f.posterUrl
                )
            ) { nav.navigate("detail/${f.slug}") }
        }
    }
}
