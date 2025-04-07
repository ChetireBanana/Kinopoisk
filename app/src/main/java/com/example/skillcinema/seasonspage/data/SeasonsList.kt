package com.example.skillcinema.seasonspage.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class SeasonsList (
    val total: Int,
    val items: List<Season>
): Parcelable