package com.example.myapplication.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ResearchPaper
import com.example.myapplication.data.repository.OncologyRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.net.SocketTimeoutException

data class DashboardUiState(
    val isLoading: Boolean = false,
    val researchCount: Int = 0,
    val clinicalTrialsCount: Int = 0,
    val fdaApprovalsCount: Int = 0,
    val newsCount: Int = 0,
    val recentPapers: List<ResearchPaper> = emptyList(),
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val repository: OncologyRepository = OncologyRepository()
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val researchResult = repository.getResearchPapers()
            val trialsResult = repository.getClinicalTrials()
            val fdaResult = repository.getFdaApprovals()
            val newsResult = repository.getNews()

            val papers = researchResult.getOrDefault(emptyList())
            val trials = trialsResult.getOrDefault(emptyList())
            val fdas = fdaResult.getOrDefault(emptyList())
            val news = newsResult.getOrDefault(emptyList())

            val hasError = researchResult.isFailure && trialsResult.isFailure && fdaResult.isFailure && newsResult.isFailure

            val errorMsg = if (hasError) {
                val exception = researchResult.exceptionOrNull() ?: trialsResult.exceptionOrNull()
                when (exception) {
                    is UnknownHostException, is SocketTimeoutException -> "Connexion réseau indisponible. Veuillez vérifier votre réseau Internet."
                    else -> "Impossible de contacter le serveur de données en oncologie. Veuillez réessayer."
                }
            } else null

            uiState = uiState.copy(
                isLoading = false,
                researchCount = papers.size,
                clinicalTrialsCount = trials.size,
                fdaApprovalsCount = fdas.size,
                newsCount = news.size,
                recentPapers = papers.take(5),
                errorMessage = errorMsg
            )
        }
    }
}
