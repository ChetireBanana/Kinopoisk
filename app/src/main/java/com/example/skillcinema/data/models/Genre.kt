package com.example.skillcinema.data.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Genre(
    val id: Int? = null,
    var genre: String,
): Parcelable

