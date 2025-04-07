package com.example.skillcinema.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class StaffList(
    val title: String,
    val items: List<StaffDto>
) : Parcelable
