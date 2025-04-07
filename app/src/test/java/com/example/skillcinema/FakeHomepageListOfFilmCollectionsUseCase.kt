package com.example.skillcinema

import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.homepage.domain.HomepageListOfFilmCollectionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FakeHomepageListOfFilmCollectionsUseCase(
) : HomepageListOfFilmCollectionsUseCase {

    private val fakeFilmCollections: MutableList<FilmCollection> = mutableListOf()
    private val fakeErrors: MutableList<String> = mutableListOf()


    private var _filmCollectionsFlow: MutableStateFlow<List<FilmCollection>> =
        MutableStateFlow(emptyList())
    override val filmCollectionsFlow = _filmCollectionsFlow.asStateFlow()

    private val _errorsFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    override val errorsFlow = _errorsFlow.asStateFlow()


    override fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)

            val collection = List(3) { it ->
                FilmCollection(
                    total = 3,
                    title = "Test Collection $it",
                    items = emptyList()
                )
            }
            fakeFilmCollections.addAll(collection)
            _filmCollectionsFlow.value = fakeFilmCollections.toList()
        }
    }

    fun sendError(errorMessage: List<String>, delay: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(delay)
            fakeErrors.addAll(errorMessage)
            _errorsFlow.value = fakeErrors.toList()
        }
    }


}