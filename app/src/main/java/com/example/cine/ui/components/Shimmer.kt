package com.example.cine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerCard() {
    Column(Modifier.width(130.dp).padding(4.dp)) {
        Box(
            Modifier
                .height(190.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .shimmer()
                .background(Color(0xFF2A2A33))
        )
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .height(12.dp)
                .fillMaxWidth(0.8f)
                .shimmer()
                .background(Color(0xFF2A2A33))
        )
    }
}

@Composable
fun ShimmerRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) { ShimmerCard() }
    }
}
