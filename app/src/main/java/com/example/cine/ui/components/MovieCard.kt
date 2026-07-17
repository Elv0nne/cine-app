package com.example.cine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cine.data.MovieItem
import com.example.cine.data.fixImage

@Composable
fun MovieCard(movie: MovieItem, onClick: () -> Unit) {
    Column(
        Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            Modifier
                .height(190.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        ) {
            AsyncImage(
                model = fixImage(movie.posterUrl.ifBlank { movie.thumbUrl }),
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.6f to Color.Transparent,
                            1f to Color(0xCC000000)
                        )
                    )
            )
            Text(
                movie.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}
