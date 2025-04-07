package com.example.skillcinema

import com.example.skillcinema.application.di.BaseApp
import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(BaseApp::class)
interface TestApplication