package com.example.skillcinema.ui


import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.screen.Screen

object HomepageScreen : Screen<HomepageScreen>() {

    val mainRootView = KView { withId(com.example.skillcinema.R.id.homepage_fragment_root_layout) }

    val homepageLoader = KView {
        withId(com.example.skillcinema.R.id.loader_error_view)
        withTag("HOME_PAGE")}

    val mainScreen = KView {
        withId(com.example.skillcinema.R.id.main_screen)
        withTag("HOME_PAGE")
    }
    }

