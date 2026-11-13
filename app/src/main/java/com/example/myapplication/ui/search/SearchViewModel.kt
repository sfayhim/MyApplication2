package com.example.myapplication.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.OncologyRepository
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val selectedFilter: String = "Tous",
    val searchResults: Map<String, List<Any>> = emptyMap(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

class SearchViewModel(
    private val repository: OncologyRepository = OncologyRepository()
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    fun onQueryChanged(newQuery: String) {
        uiState = uiState.copy(query = newQuery)
    }

    fun onFilterSelected(filter: String) {
        uiState = uiState.copy(selectedFilter = filter)
    }

    fun performSearch(initialQuery: String? = null) {
        val q = initialQuery ?: uiState.query
        if (q.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, hasSearched = true, query = q)
            val result = repository.searchAll(q)
            if (result.isSuccess) {
                uiState = uiState.copy(
                    isLoading = false,
                    searchResults = result.getOrDefault(emptyMap())
                )
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erreur lors de la recherche. Veuillez réessayer."
                )
            }
        }
    }
}
