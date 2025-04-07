package com.example.skillcinema.application.presentation

import android.content.Context
import com.example.skillcinema.R


const val PREFERENCES_NAME = "prefs_name"
const val KEY_FIRST_LAUNCH = "first_launch"
const val MAX_ELEMENT_IN_HOMEPAGE_LIST = 20
const val MAX_ELEMENT_IN_FILM_PAGE_GALLERY_LIST = 20
const val YEAR_PICKER_SPAN_COUNT = 4

fun professionKeyTranslator(context: Context, professionKey: String): String {
    return when (professionKey) {
        "WRITER" -> context.getString(R.string.profession_key_writer)
        "OPERATOR" -> context.getString(R.string.profession_key_operator)
        "EDITOR" -> context.getString(R.string.profession_key_editor)
        "COMPOSER" -> context.getString(R.string.profession_key_composer)
        "PRODUCER_USSR" -> context.getString(R.string.profession_key_produsser_ussr)
        "HIMSELF" -> context.getString(R.string.profession_key_himself)
        "HERSELF" -> context.getString(R.string.profession_key_herself)
        "HRONO_TITR_MALE" -> context.getString(R.string.profession_key_titr_male)
        "HRONO_TITR_FEMALE" -> context.getString(R.string.profession_key_titr_female)
        "TRANSLATOR" -> context.getString(R.string.profession_key_translator)
        "DIRECTOR" -> context.getString(R.string.profession_key_director)
        "DESIGN" -> context.getString(R.string.profession_key_designer)
        "PRODUCER" -> context.getString(R.string.profession_key_producer)
        "ACTOR" -> context.getString(R.string.profession_key_actor)
        "VOICE_DIRECTOR" -> context.getString(R.string.profession_key_voice_director)
        "UNKNOWN" -> context.getString(R.string.profession_key_unknown)
        else -> context.getString(R.string.profession_key_unknown)
    }
}

fun getPossibleImagesTypesList(): List<String>{
    return listOf(
        "STILL",
        "SHOOTING",
        "POSTER",
        "FAN_ART",
        "PROMO",
        "CONCEPT",
        "WALLPAPER",
        "COVER",
        "SCREENSHOT"
    )
}

fun imagesTypesTranslator(context: Context, imagesType: String): String {
    return when (imagesType) {
        "STILL" -> context.getString(R.string.images_type_still)
        "SHOOTING" -> context.getString(R.string.images_type_shooting)
        "POSTER" -> context.getString(R.string.images_type_poster)
        "FAN_ART" -> context.getString(R.string.images_type_fan_art)
        "PROMO" -> context.getString(R.string.images_type_promo)
        "CONCEPT" -> context.getString(R.string.images_type_concept)
        "WALLPAPER" -> context.getString(R.string.images_type_wallpaper)
        "COVER" -> context.getString(R.string.images_type_cover)
        "SCREENSHOT" -> context.getString(R.string.images_type_screenshot)
        else -> context.getString(R.string.images_type_unknown)
    }
}










