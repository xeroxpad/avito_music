package com.example.avito.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.avito.R
import com.example.avito.entity.TrackCard

@Composable
fun CardTrack(
    modifier: Modifier = Modifier,
    trackCard: TrackCard,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape = RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 3.dp, horizontal = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(Color.Gray.copy(0.2f))
                    .weight(0.2f),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = trackCard.coverTrack,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shape = RoundedCornerShape(14.dp))
                        .padding(3.dp),
                    placeholder = painterResource(id = R.drawable.ic_track_default),
                    error = painterResource(id = R.drawable.ic_track_default),
                    contentScale = ContentScale.FillBounds,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.8f)
            ) {
                Column {
                    Text(
                        text = trackCard.titleTrack,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = trackCard.artistTrack,
                        fontWeight = FontWeight.Light,
                        color = Color.Gray,
                        lineHeight = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}