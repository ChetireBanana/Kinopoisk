package com.example.skillcinema.data.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Country(
    val id: Int? = null,
    val country: String,
) : Parcelable
