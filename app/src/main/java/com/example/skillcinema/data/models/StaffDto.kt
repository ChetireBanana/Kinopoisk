package com.example.skillcinema.data.models

import android.os.Parcelable
import com.example.skillcinema.domain.entity.StaffModel
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
class StaffDto(
    override val staffId: Int,
    override val nameRu: String? = null,
    override val nameEn: String? = null,
    override val description: String? = null,
    override val posterUrl: String,
    override val professionText: String,
    override val professionKey: String

) : StaffModel, Parcelable

