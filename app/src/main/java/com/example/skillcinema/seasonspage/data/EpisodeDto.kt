package com.example.skillcinema.seasonspage.data

import android.os.Parcelable
import com.example.skillcinema.domain.entity.EpisodeModel
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class EpisodeDto(
    override val seasonNumber: Int,
    override val episodeNumber: Int,
    override val nameRu: String? = null,
    override val nameEn: String? = null,
    override val synopsis: String? = null,
    override val releaseDate: String? = null,
) : EpisodeModel, Parcelable