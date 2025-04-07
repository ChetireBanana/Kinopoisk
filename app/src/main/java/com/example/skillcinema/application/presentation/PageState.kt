package com.example.skillcinema.application.presentation

sealed class PageState {
    data object PageLoading : PageState()
    data object PageReady : PageState()
    data object PageError : PageState()
}