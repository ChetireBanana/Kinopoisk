package com.example.skillcinema.domain.entity

interface StaffModel {
    val staffId: Int
    val nameRu: String?
    val nameEn: String?
    val description: String?
    val posterUrl: String
    val professionText: String?
    val professionKey: String?
}