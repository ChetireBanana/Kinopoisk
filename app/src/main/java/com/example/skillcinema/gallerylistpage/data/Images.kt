package com.example.skillcinema.gallerylistpage.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Images (
    val imageUrl: String,
    val previewUrl: String
): Parcelable