package com.example.skillcinema.data.models

import android.os.Parcelable
import com.example.skillcinema.domain.entity.PersonInfoFilmModel
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class PersonInfoFilmDto(
    override val filmId: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val rating: String?,
    override val description: String?,
    override val professionKey: String,
    override var posterUrl: String? = null,
    override var posterUrlPreview: String? = null,
    override var genre: String?,
    override var year: Int?,
) : PersonInfoFilmModel, Parcelable

