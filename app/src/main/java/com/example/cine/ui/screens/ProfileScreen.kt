package com.example.cine.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy. LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui. Modifier
import androidx.compose.ui.platform. LocalContext
import androidx.compose.ui.text.font. FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation. NavController
import com.example.cine.data. AppDatabase

@Composable
fun ProfileScreen(nav: NavController) {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).dao() }
    val history by dao.history().collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Cá nhân", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight. Black)
        Spacer(Modifier.height(16.dp))
        Text("Đang xem tiếp", style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight. Bold)
        Spacer(Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text("Chưa có lịch sử xem")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history) { h ->
                    ListItem(
                        headlineContent = { Text(h.name, maxLines = 1) },
                        supportingContent = {
                            val pct = if (h.durationMs > 0)
                                (h.positionMs * 100 / h.durationMs) else 0
                            Text("${h.episodeName} • đã xem $pct%")
                        },
                        modifier = Modifier.clickable { nav.navigate("detail/${h.slug}") }
                    )
                }
            }
        }
    }
}
