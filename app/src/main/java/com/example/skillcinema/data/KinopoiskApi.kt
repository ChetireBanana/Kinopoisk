package com.example.skillcinema.data

import android.util.Log
import com.example.skillcinema.data.models.StaffDto
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.gallerylistpage.data.ImagesCollection
import com.example.skillcinema.seasonspage.data.SeasonsList
import com.example.skillcinema.data.models.SimilarFilmsCollection
import com.example.skillcinema.data.models.FiltersGenresCountries
import com.example.skillcinema.personalpage.data.PersonInfoDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

private const val BASE_URL = "https://kinopoiskapiunofficial.tech"

private const val API_KEY = "fec4ea7d-8b5e-4d70-a4c8-9d79e4710dbb"
//private const val API_KEY = "3a1eebec-bea5-4f52-95fb-6ee13ff6314a"
//private const val API_KEY = "0a4e812d-0297-4049-b955-008516188f6e"
//private const val API_KEY = "364fb70f-c8dc-467d-b3e3-67188f2dc69c"
//private const val API_KEY = "47cc9ccb-030a-4c11-a021-f12f1eb068ea"
//private const val API_KEY = "47cc9ccb-030a-4c11-a021-f12f1eb068ea"
//private const val API_KEY = "a15f1bd3-de14-4d96-9d64-74bdcbc215da"
//private const val API_KEY = "75d78882-0db2-4502-8719-5b7de9029d3d"

object RetrofitInstance {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val kinopoiskApi: KinopoiskApi = retrofit.create(KinopoiskApi::class.java)
}

interface KinopoiskApi {


    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/premieres")
    suspend fun getPremiers(
        @Query("year") year: Int,
        @Query("month") month: String
    ): Response<FilmCollection>{
        Log.d("KinopoiskApi getPremiers", "Запрос к API getPremiers(year=$year, month=$month)")
        return try {
            val response = this@KinopoiskApi.getPremiers(year, month)
            Log.d("KinopoiskApi getPremiers", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getPremiers", "Ошибка при выполнении запроса", e)
            throw e
        }
    }


    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/collections")
    suspend fun getTopListFilmCollection(
        @QueryMap filters: HashMap<String, String?>,
        @Query("page") page: Int,
    ): Response<FilmCollection>{
        Log.d("KinopoiskApi getTopListFilmCollection", "Запрос к API getTopListFilmCollection($filters, page=$page)")
        return try {
            val response = this@KinopoiskApi.getTopListFilmCollection(filters, page)
            Log.d("KinopoiskApi getTopListFilmCollection", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getTopListFilmCollection", "Ошибка при выполнении запроса", e)
            throw e
        }
    }


    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films")
    suspend fun getFilmsAndSerialsByFilters(
        @QueryMap filters: HashMap<String, String?>,
        @Query("page") page: Int,
    ): Response<FilmCollection>{
        Log.d("KinopoiskApi getFilmsAndSerialsByFilters", "Запрос к API getFilmsAndSerialsByFilters($filters, page=$page)")
        return try {
            val response = this@KinopoiskApi.getFilmsAndSerialsByFilters(filters, page)
            Log.d("KinopoiskApi getFilmsAndSerialsByFilters", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getFilmsAndSerialsByFilters", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/filters")
    suspend fun getFilters(): Response<FiltersGenresCountries> {
        Log.d("KinopoiskApi getFilters", "Запрос к API getFilters()")
        return try {
            val response = this@KinopoiskApi.getFilters()
            Log.d("KinopoiskApi getFilters", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getFilters", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/{id}")
    suspend fun getFilmInfo(
        @Path("id") id: Int
    ): Response<FilmDto> {
        println("KinopoiskApi getFilmInfo")
        Log.i("KinopoiskApi getFilmInfo", "Запрос к API getFilmInfo(id=$id)")
        return try {
            val response = this@KinopoiskApi.getFilmInfo(id)
            if (response.isSuccessful){
                if (response.isSuccessful) {
                    Log.i("KinopoiskApi getFilmInfo", "Ответ получен успешно: ${response.body()}")
                } else {
                    Log.w("KinopoiskApi getFilmInfo", "Ответ получен с ошибкой: ${response.code()} ${response.message()}")
                }
            }
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getFilmInfo", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/{id}/seasons")
    suspend fun getSeasonsInfo(
        @Path("id") id: Int
    ): Response<SeasonsList> {
        Log.d("KinopoiskApi getSeasonsInfo", "Запрос к API getSeasonsInfo(id=$id)")
        return try {
            val response = this@KinopoiskApi.getSeasonsInfo(id)
            Log.d("KinopoiskApi getSeasonsInfo", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getSeasonsInfo", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v1/staff")
    suspend fun getStaff(
        @Query("filmId") filmId: Int
    ): Response<List<StaffDto>> {
        Log.d("KinopoiskApi getStaff", "Запрос к API getStaff(filmId=$filmId)")
        return try {
            val response = this@KinopoiskApi.getStaff(filmId)
            Log.d("KinopoiskApi getStaff", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getStaff", "Ошибка при выполнении запроса", e)
            throw e
        }
    }


    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v1/staff/{id}")
    suspend fun getPersonInfo(
        @Path("id") id: Int
    ): Response<PersonInfoDto> {
        Log.d("KinopoiskApi getPersonInfo", "Запрос к API getPersonInfo(id=$id)")
        return try {
            val response = this@KinopoiskApi.getPersonInfo(id)
            Log.d("KinopoiskApi getPersonInfo", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getPersonInfo", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/{id}/images")
    suspend fun getFilmImages(
        @Path("id") id: Int,
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<ImagesCollection> {
        Log.d(
            "KinopoiskApi getFilmImages",
            "Запрос к API getFilmImages(id=$id, type=$type, page=$page)"
        )
        return try {
            val response = this@KinopoiskApi.getFilmImages(id, type, page)
            Log.d("KinopoiskApi getFilmImages", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getFilmImages", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

    @Headers("X-API-KEY:$API_KEY")
    @GET("/api/v2.2/films/{id}/similars")
    suspend fun getSimilarFilms(
        @Path("id") id: Int,
    ):Response<SimilarFilmsCollection> {
        Log.d("KinopoiskApi getSimilarFilms", "Запрос к API getSimilarFilms(id=$id)")
        return try {
            val response = this@KinopoiskApi.getSimilarFilms(id)
            Log.d("KinopoiskApi getSimilarFilms", "Ответ получен успешно")
            response
        } catch (e: Exception) {
            Log.e("KinopoiskApi getSimilarFilms", "Ошибка при выполнении запроса", e)
            throw e
        }
    }

}