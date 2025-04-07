package com.example.skillcinema.domain.entity

import com.example.skillcinema.data.models.FilmDto

interface FilmCollectionModel {
    var total: Int
    var totalPages: Int?
    var items: List<FilmDto>
    val title: String?
    val filters: Map<String, String?>?
    val tag: String?
}