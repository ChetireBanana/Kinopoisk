package com.example.skillcinema.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PersonInfoFilmDtoCollection(
    var title: String,
    var films: List<PersonInfoFilmDto>
) : Parcelable
