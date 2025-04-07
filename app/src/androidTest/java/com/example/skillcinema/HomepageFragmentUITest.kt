package com.example.skillcinema


import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.homepage.presentation.HomepageFragment
import com.example.skillcinema.homepage.presentation.HomepageViewModel
import com.example.skillcinema.ui.HomepageScreen
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class HomepageFragmentUITest
    : TestCase(kaspressoBuilder = Kaspresso.Builder.simple().apply{
}) {


    private lateinit var homepageViewModel: HomepageViewModel
    private lateinit var fakeRepository: FakeLocalDBRepository
    private lateinit var fakeUseCase: FakeHomepageListOfFilmCollectionsUseCase

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()

        fakeRepository = FakeLocalDBRepository()
        fakeUseCase = FakeHomepageListOfFilmCollectionsUseCase()
        homepageViewModel = HomepageViewModel(fakeUseCase, fakeRepository)
        homepageViewModel.resetState()
    }


    @Test
    fun fragmentDisplays() = run {

        step("Запускаем фрагмент главной страницы") {
            launchFragmentInHiltContainer<HomepageFragment>(
                themeResId = R.style.Theme_Skillcinema
            )
        }

        step("Проверяем что фрагмент отобразился") {
            //espresso test
//            onView(withId(R.id.root_view))
//                .check(matches(isDisplayed()))

            //kaspresso test
            HomepageScreen.mainRootView.isVisible()
        }
    }

    @Test
    fun stateObserver_LoadingAndReady() = run {
        step("Запускаем фрагмент главной страницы") {
            launchFragmentInHiltContainer<HomepageFragment>(
                themeResId = R.style.Theme_Skillcinema
            )
        }

        step("Проверяем состояние PageLoading loader visible") {
            homepageViewModel.createListOfFilmCollections()

            HomepageScreen.mainScreen.isGone()
            HomepageScreen.homepageLoader.isVisible()
        }


        step("Проверяем переход в состояние PageReady") {
            HomepageScreen.mainScreen.isVisible()
            HomepageScreen.homepageLoader.isGone()
        }
    }

    @Test
    fun testStateObserver_Error() = run {
        step("Запускаем фрагмент главной страницы") {
            launchFragmentInHiltContainer<HomepageFragment>(
                themeResId = R.style.Theme_Skillcinema
            )
        }


        step("Проверяем состояние PageError") {
            homepageViewModel.createListOfFilmCollections()

            fakeUseCase.sendError(
                List(3) { "Error $it" }, 500
            )

            runBlocking {
                homepageViewModel.errorsFlow.first { it.isNotEmpty() }
                homepageViewModel.pageState.first { it == PageState.PageReady }
                println("Errors: ${homepageViewModel.errorsFlow.value}")
            }


            HomepageScreen.mainScreen {
                flakySafely(timeoutMs = 5000) { isVisible() }
            }

            HomepageScreen.homepageLoader {
                flakySafely(timeoutMs = 5000) { isGone() }
            }
        }


    }

    @Test
    fun testStateObserver_ErrorOverfill() = run {
        step("Запускаем фрагмент главной страницы") {
            launchFragmentInHiltContainer<HomepageFragment>(
                themeResId = R.style.Theme_Skillcinema
            )
        }

        step("Проверяем состояние PageError overfill") {
            homepageViewModel.createListOfFilmCollections()
            fakeUseCase.sendError(
                List(6) { "Error $it" }, 500
            )

            runBlocking {
                homepageViewModel.errorsFlow.first { it.isNotEmpty() }
                homepageViewModel.pageState.first { it == PageState.PageError }
                println("________ДОЛЖНА БЫТЬ ОШИБКА_________: ${homepageViewModel.errorsFlow.value}")
                println("Before mainScreen.isGone() context: ${currentCoroutineContext()}")
            }

            println("Before mainScreen.isGone()")


            HomepageScreen.mainScreen {
                flakySafely(timeoutMs = 5000) { isGone() }
            }

            HomepageScreen.homepageLoader {
                flakySafely(timeoutMs = 5000) { isVisible() }
            }
        }
    }
}


