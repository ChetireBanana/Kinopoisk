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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


@OptIn(ExperimentalCoroutinesApi::class)
class HomepageViewModelMockTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var homepageViewModel: HomepageViewModel
    private val mockUseCase: HomepageListOfFilmCollectionsUseCase = mock()
    private val mockLocalDataBaseRepository: LocalDBRepository = mock()
    private lateinit var filmCollectionsFlow: MutableStateFlow<List<FilmCollection>>
    private lateinit var errorsFlow: MutableStateFlow<List<String>>

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        filmCollectionsFlow = MutableStateFlow(emptyList())
        `when`(mockUseCase.filmCollectionsFlow).thenReturn(filmCollectionsFlow)
        errorsFlow = MutableStateFlow(emptyList())
        `when`(mockUseCase.errorsFlow).thenReturn(errorsFlow)

        homepageViewModel = HomepageViewModel(mockUseCase, mockLocalDataBaseRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        runTest {
            assertEquals(PageState.PageLoading, homepageViewModel.pageState.value)
        }
    }


    @Test
    fun testCreateListOfFilmCollections() =
        runTest {
            val testData =
                List(5) { FilmCollection(5, items = emptyList(), title = "testCollection") }

            assertEquals(PageState.PageLoading, homepageViewModel.pageState.value)
            filmCollectionsFlow.value = testData

            advanceUntilIdle()

            assertEquals(PageState.PageReady, homepageViewModel.pageState.value)
            assertEquals(testData, homepageViewModel.filmCollectionsFlow.value)
        }



    @Test

    fun clearErrorListWhenLeaveScreenHandling() {
        runTest {
            val errorMessage = List(3) { "Error $it" }

            homepageViewModel.createListOfFilmCollections()

            errorsFlow.value = errorMessage

            runCurrent()

            assertEquals(errorMessage, homepageViewModel.errorsFlow.value)

            homepageViewModel.clearErrorsWhenLeaveScreen()
            runCurrent()

            assertEquals(emptyList<String>(), homepageViewModel.errorsFlow.value)
        }
    }

    @Test
    fun testCheckIfViewed() {
        checkIfViewedHandler(true)
    }

    @Test
    fun testCheckIfNotViewed() {
        checkIfViewedHandler(false)
    }

    private fun checkIfViewedHandler(expectedResult: Boolean) {
        runTest {
            val testedId = 1
            val testDispatcher = StandardTestDispatcher(testScheduler)

            `when`(
                mockLocalDataBaseRepository.checkIfItemIsInCollection(
                    testedId,
                    LocalBaseCollections.VIEWED_ID
                )
            )
                .thenReturn(expectedResult)

            val callback: (Boolean) -> Unit = mock()

            homepageViewModel.checkIfViewed(testedId, callback, testDispatcher)

            advanceUntilIdle()

            Mockito.verify(callback).invoke(expectedResult)

        }
    }

}