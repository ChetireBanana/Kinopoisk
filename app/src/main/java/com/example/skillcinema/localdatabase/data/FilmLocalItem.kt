package com.example.skillcinema.localdatabase.data

data class FilmLocalItem(
override val id: Int,
val title: String,
val genre: String?,
val rating: Float?,
val posterUrl: String?,
val year: Int?,
) : ItemInterface {
    override val type: String = "FILM"
}

