package com.example.skillcinema.searchfragment.data

import com.example.skillcinema.data.models.Country
import com.example.skillcinema.data.models.Genre
import com.example.skillcinema.data.models.extras.FiltersOrder
import com.example.skillcinema.data.models.extras.FiltersTypesOfFilms

data class FilterOptionsRepository(
    var countries: Country? = null,
    var genres: Genre? = null,
    var order: String = FiltersOrder.RATING,
    var type: String = FiltersTypesOfFilms.ALL,
    var ratingFrom: Float = 1.0f,
    var ratingTo: Float = 10.0f,
    var yearFrom: Int? = null,
    var yearTo: Int? = null,
    var filterNoViewed: Boolean = false
)


