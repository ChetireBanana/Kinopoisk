package com.example.skillcinema.data.models

import android.os.Parcelable
import com.example.skillcinema.domain.entity.FilmModel
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FilmDto(
    override val kinopoiskId: Int,
    override val nameRu: String? = null,
    override val nameOriginal: String? = null,
    override val countries: List<Country> = emptyList(),
    override val genres: List<Genre> = emptyList(),
    override val posterUrl: String? = null,
    override val posterUrlPreview: String? = null,
    override val webUrl: String? = null,
    //Additional fields
    override val ratingKinopoisk: Float? = null,
    override val ratingImdb: Float? = null,
    override val year: Int? = null,
    override val logoUrl: String? = null,
    override val filmLength: Int? = null,
    override val description: String? = null,
    override val shortDescription: String? = null,
    override val type: String? = null,
    override val ratingAgeLimits: String? = null,
    override val serial: Boolean = false,
    //Rare fields
    override val premiereRu: String? = null,

    ) : FilmModel, Parcelable