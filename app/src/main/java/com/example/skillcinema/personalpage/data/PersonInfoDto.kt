package com.example.skillcinema.personalpage.data

import android.os.Parcelable
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.domain.entity.PersonInfoModel
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class PersonInfoDto(
    override val personId: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val posterUrl: String?,
    override val profession: String?,
    override var films: List<PersonInfoFilmDto>?
):PersonInfoModel,Parcelable
