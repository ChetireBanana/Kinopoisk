package com.example.skillcinema.domain.entity

interface PersonInfoFilmModel {
    val filmId: Int
    val nameRu: String?
    val nameEn: String?
    val rating: String?
    val description: String?
    val professionKey: String
    var posterUrl: String?
    var posterUrlPreview: String?
    var genre: String?
    var year: Int?
}