package com.example.skillcinema.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimilarFilms(
    val filmId: Int,
    val nameRu: String? = null,
    val nameEn: String?= null,
    val nameOriginal: String ? = null,
    val posterUrl: String,
    val posterUrlPreview: String,
)
