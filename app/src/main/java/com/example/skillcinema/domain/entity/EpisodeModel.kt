package com.example.skillcinema.domain.entity

interface EpisodeModel {
    val seasonNumber: Int
    val episodeNumber: Int
    val nameRu: String?
    val nameEn: String?
    val synopsis: String?
    val releaseDate: String?
}