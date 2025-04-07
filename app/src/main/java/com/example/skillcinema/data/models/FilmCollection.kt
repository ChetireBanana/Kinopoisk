package com.example.skillcinema.data.models

import android.os.Parcelable
import com.example.skillcinema.domain.entity.FilmCollectionModel
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FilmCollection(
    override var total: Int,
    override var totalPages: Int? = null,
    override var items: List<FilmDto>,
    override var title: String? = null,
    override val filters: Map<String, String?>? = null,
    override var tag: String? = null,
    ) : Parcelable, FilmCollectionModel
