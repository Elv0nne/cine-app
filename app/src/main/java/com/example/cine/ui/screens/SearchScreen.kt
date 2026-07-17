package com.example.cine.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cine.ui.MovieViewModel
import com.example.cine.ui.components.MovieCard

@Composable
fun SearchScreen(vm: MovieViewModel, nav: NavController) {
    val result by vm.searchResult.collectAsState()
    var q by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(
            value = q,
            onValueChange = { q = it },
            label = { Text("Tìm phim...") },
            trailingIcon = {
                TextButton(onClick = { vm.search(q) }) { Text("Tìm") }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(result) { m ->
                MovieCard(m) { nav.navigate("detail/${m.slug}") }
            }
        }
    }
}
