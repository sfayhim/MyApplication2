package com.example.myapplication.ui.results

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.OncologyRepository
import kotlinx.coroutines.launch

data class ResultsUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val selectedTab: String = "Tous",
    val searchResults: Map<String, List<Any>> = emptyMap(),
    val errorMessage: String? = null
)

class ResultsViewModel(
    private val repository: OncologyRepository = OncologyRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ResultsUiState())
        private set

    fun loadResults(query: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, query = query, errorMessage = null)
            val result = repository.searchAll(query)
            if (result.isSuccess) {
                uiState = uiState.copy(
                    isLoading = false,
                    searchResults = result.getOrDefault(emptyMap())
                )
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erreur de chargement des résultats."
                )
            }
        }
    }

    fun selectTab(tab: String) {
        uiState = uiState.copy(selectedTab = tab)
    }
}
