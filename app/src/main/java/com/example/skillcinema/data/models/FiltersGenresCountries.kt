package com.example.skillcinema.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FiltersGenresCountries(
    val genres: List<Genre>,
    val countries: List<Country>
)