package com.example.skillcinema.localdatabase.data

data class PersonLocalItem(
override val id: Int,
val name: String,
val profession: String,
val posterUrl: String,
) : ItemInterface {
    override val type: String = "PERSON"
}

