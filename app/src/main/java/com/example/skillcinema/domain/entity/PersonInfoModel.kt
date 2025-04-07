package com.example.skillcinema.domain.entity

import com.example.skillcinema.data.models.PersonInfoFilmDto

interface PersonInfoModel {
    val personId: Int
    val nameRu: String?
    val nameEn: String?
    val posterUrl: String?
    val profession: String?
    val films: List<PersonInfoFilmDto>?
}