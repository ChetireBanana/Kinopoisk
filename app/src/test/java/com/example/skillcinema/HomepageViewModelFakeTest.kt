package com.example.skillcinema

import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.homepage.domain.HomepageListOfFilmCollectionsUseCase
import com.example.skillcinema.homepage.presentation.HomepageViewModel
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.profilefragment.data.LocalDBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


@OptIn(ExperimentalCoroutinesApi::class)
class HomepageViewModelFakeTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var homepageViewModel: HomepageViewModel
    private lateinit var fakeRepository: FakeLocalDBRepository
    private lateinit var fakeUseCase: FakeHomepageListOfFilmCollectionsUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeLocalDBRepository()
        fakeUseCase = FakeHomepageListOfFilmCollectionsUseCase()
        homepageViewModel = HomepageViewModel(fakeUseCase, fakeRepository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun emptyErrorHandling() {
        val errorMessage = emptyList<String>()
        testErrorHandling(errorMessage, PageState.PageReady)
    }

    @Test
    fun someErrorHandling() {
        val errorMessage = List(3) { "Error $it" }
        testErrorHandling(errorMessage, PageState.PageReady, 600)
    }


    @Test
    fun testErrorOverfillHandling() {
        val errorMessage = List(6) { "Error $it" }
        testErrorHandling(errorMessage, PageState.PageError, 600)
    }

    private fun testErrorHandling(errorMessage: List<String>, expectedState: PageState, delay: Long = 0) {
       runTest {

            assertTrue(homepageViewModel.errorsFlow.value.isEmpty())
            assertTrue(homepageViewModel.pageState.value !is PageState.PageError)

            homepageViewModel.createListOfFilmCollections()
            fakeUseCase.sendError(errorMessage, delay)

            advanceUntilIdle()

            assertEquals(expectedState, homepageViewModel.pageState.value)
            val errors = homepageViewModel.errorsFlow.value
            assertEquals(errorMessage.size, errors.size)
            assertEquals(errorMessage, errors)
        }
    }


    @Test
    fun testViewModel_FullIntegration() = runTest {
        val fakeRepository = FakeLocalDBRepository()
        val fakeUseCase = FakeHomepageListOfFilmCollectionsUseCase()
        val homepageViewModel = HomepageViewModel(fakeUseCase, fakeRepository)

        homepageViewModel.createListOfFilmCollections()

        advanceUntilIdle()

        assertTrue(homepageViewModel.pageState.value is PageState.PageReady)
        assert(homepageViewModel.errorsFlow.value.isEmpty())
    }
}