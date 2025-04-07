package com.example.skillcinema.domain.entity

import com.example.skillcinema.data.models.Country
import com.example.skillcinema.data.models.Genre

interface FilmModel {
    //EveryRequestInfo
    val kinopoiskId: Int
    val nameRu: String?
    val nameOriginal: String?
    val countries: List<Country>
    val genres: List<Genre>
    val year: Int?
    val posterUrl: String?
    val posterUrlPreview: String?
    val webUrl: String?
    //AdditionalDescription
    val ratingKinopoisk: Float?
    val ratingImdb: Float?
    val logoUrl: String?
    val filmLength: Int?
    val description: String?
    val shortDescription: String?
    val type: String?
    val ratingAgeLimits: String?
    val serial: Boolean
    //PremiersOnly
    val premiereRu: String?
}

