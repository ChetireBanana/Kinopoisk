package com.example.skillcinema.gallerylistpage.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ImagesCollection(
    var type: String? = null,
    val total: Int,
    val totalPages: Int,
    val items: List<Images>
): Parcelable
