package com.example.skillcinema.localdatabase.entities


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skillcinema.localdatabase.data.FilmLocalItem
import com.example.skillcinema.localdatabase.data.PersonLocalItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item_table")
data class LocalItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val itemId: Int,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "genre")
    val genre: String? = null,
    @ColumnInfo(name = "rating")
    val rating: Float? = null,
    @ColumnInfo(name = "year")
    val year: Int?,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "profession")
    val profession: String? = null,
    @ColumnInfo(name = "poster_url")
    val posterUrl: String? = null,


    ):Parcelable {
    companion object {
        fun fromFilm(film: FilmLocalItem): LocalItemEntity {
            return LocalItemEntity(
                itemId = film.id,
                type = film.type,
                title = film.title,
                genre = film.genre,
                rating = film.rating,
                year = film.year,
                name = null,
                profession = null,
                posterUrl = film.posterUrl
            )
        }

        fun fromPerson(person: PersonLocalItem): LocalItemEntity {
            return LocalItemEntity(
                itemId = person.id,
                type = person.type,
                title = null,
                genre = null,
                rating = null,
                year = null,
                name = person.name,
                profession = person.profession,
                posterUrl = person.posterUrl
            )
        }
    }
}


