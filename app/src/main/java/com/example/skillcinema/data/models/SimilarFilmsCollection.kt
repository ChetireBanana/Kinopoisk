package com.example.skillcinema.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimilarFilmsCollection(
    val total: Int,
    val items: List<SimilarFilms>
)
