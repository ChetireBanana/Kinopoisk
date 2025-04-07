package com.example.skillcinema.application.presentation


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import com.example.skillcinema.R


@Composable
fun GlideImageWithPlaceholder(
    imageUrl: String?,
    width: Int,
    height: Int,
    modifier: Modifier = Modifier
) {
    if (!imageUrl.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .size(width = width.dp, height = height.dp)
                .background(color = MaterialTheme.colorScheme.surfaceDim),
            contentAlignment = Alignment.Center,
        ){
            Image(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = "PlaceHolder",
                modifier = Modifier.size(50.dp)
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Image",
            modifier = modifier
                .size(width = width.dp, height = height.dp)
        )
    }




}