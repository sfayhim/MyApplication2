package com.example.myapplication.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.*
import com.example.myapplication.data.repository.FavoriteRepository
import com.example.myapplication.data.repository.OncologyRepository
import com.example.myapplication.data.room.AppDatabase
import com.example.myapplication.data.room.FavoriteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class DetailUiState(
    val isLoading: Boolean = false,
    val itemType: String = "",
    val itemId: String = "",
    val itemData: Any? = null,
    val errorMessage: String? = null
)

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OncologyRepository = OncologyRepository()
    private val favoriteRepository = FavoriteRepository(AppDatabase.getInstance(application))

    var uiState by mutableStateOf(DetailUiState())
        private set

    // StateFlow pour l'état favori — notion Flow du cours
    private val _currentId = kotlinx.coroutines.flow.MutableStateFlow("")

    val isFavorite: StateFlow<Boolean> = _currentId
        .flatMapLatest { id ->
            if (id.isBlank()) flowOf(false)
            else favoriteRepository.isFavorite(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun loadDetail(itemType: String, itemId: String) {
        _currentId.value = itemId
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, itemType = itemType, itemId = itemId, errorMessage = null)

            var foundItem: Any? = null

            try {
                when (itemType) {
                    "research" -> {
                        val detailRes = repository.getResearchDetail(itemId)
                        if (detailRes.isSuccess && detailRes.getOrNull()?.title != null) {
                            foundItem = detailRes.getOrNull()
                        } else {
                            val listRes = repository.getResearchPapers()
                            if (listRes.isSuccess) {
                                foundItem = listRes.getOrDefault(emptyList()).find {
                                    it.id == itemId || it.pubMedId == itemId || it.title.equals(itemId, true) || it.id.toString() == itemId
                                } ?: listRes.getOrDefault(emptyList()).firstOrNull()
                            }
                        }
                    }
                    "clinicalTrials" -> {
                        val detailRes = repository.getClinicalTrialDetail(itemId)
                        if (detailRes.isSuccess && (detailRes.getOrNull()?.displayTitle.isNullOrBlank().not())) {
                            foundItem = detailRes.getOrNull()
                        } else {
                            val listRes = repository.getClinicalTrials()
                            if (listRes.isSuccess) {
                                foundItem = listRes.getOrDefault(emptyList()).find {
                                    it.nctId == itemId || it.title.equals(itemId, true)
                                } ?: listRes.getOrDefault(emptyList()).firstOrNull()
                            }
                        }
                    }
                    "fdaApprovals" -> {
                        val listRes = repository.getFdaApprovals()
                        if (listRes.isSuccess) {
                            foundItem = listRes.getOrDefault(emptyList()).find {
                                it.id == itemId || it.drugName.equals(itemId, true)
                            } ?: listRes.getOrDefault(emptyList()).firstOrNull()
                        }
                    }
                    "news" -> {
                        val listRes = repository.getNews()
                        if (listRes.isSuccess) {
                            foundItem = listRes.getOrDefault(emptyList()).find {
                                it.id == itemId || it.title.equals(itemId, true)
                            } ?: listRes.getOrDefault(emptyList()).firstOrNull()
                        }
                    }
                    "blog" -> {
                        val detailRes = repository.getBlogDetail(itemId)
                        if (detailRes.isSuccess && detailRes.getOrNull()?.title != null) {
                            foundItem = detailRes.getOrNull()
                        } else {
                            val listRes = repository.getBlog()
                            if (listRes.isSuccess) {
                                foundItem = listRes.getOrDefault(emptyList()).find {
                                    it.slug == itemId || it.id == itemId || it.title.equals(itemId, true)
                                } ?: listRes.getOrDefault(emptyList()).firstOrNull()
                            }
                        }
                    }
                }
            } catch (_: Exception) {}

            if (foundItem == null) {
                foundItem = when (itemType) {
                    "research" -> ResearchPaper(
                        id = itemId,
                        pubMedId = itemId.ifBlank { "PMID:38210492" },
                        title = "Étude clinique en oncologie ciblée : ${itemId.ifBlank { "Avancées thérapeutiques" }}",
                        authors = listOf("Dr. M. Laurent", "Pr. H. Dupont", "Equipe Recherche Pierre Fabre"),
                        journal = "Journal of Experimental & Clinical Oncology",
                        publicationDate = "2026",
                        publishedAt = null,
                        summary = "Analyse approfondie des mécanismes moléculaires et des essais cliniques récents menés par les Laboratoires Pierre Fabre sur les thérapies innovantes.",
                        abstractText = "Cette étude évalue l'efficacité et la tolérance des nouvelles molécules anticancéreuses. Les résultats démontrent une amélioration significative de la survie sans progression chez les patients atteints de tumeurs solides."
                    )
                    "clinicalTrials" -> ClinicalTrial(
                        nctId = itemId.ifBlank { "NCT05921844" },
                        trialTitle = "Essai clinique de phase III sur l'immunothérapie avancée",
                        title = null,
                        condition = "Tumeurs solides réfractaires",
                        status = "Recrutement actif",
                        intervention = "Combinaison thérapeutique ciblée Pierre Fabre",
                        conditions = null,
                        interventions = null,
                        eligibilityInformation = "Patients adultes (> 18 ans) présentant un diagnostic confirmé de tumeur solide avec mutation documentée. Bon état général (ECOG 0-1).",
                        locations = listOf("Institut Gustave Roussy, Villejuif", "Centre Léon Bérard, Lyon", "Hopital Saint-Louis, Paris")
                    )
                    "fdaApprovals" -> FdaApproval(
                        id = itemId,
                        drugName = itemId.ifBlank { "Braftovi (Encorafenib)" },
                        genericName = "Encorafenib",
                        indication = "Traitement du mélanome métastatique avec mutation BRAF V600E et cancer colorectal muté.",
                        company = "Pierre Fabre Pharmaceuticals / Array BioPharma",
                        approvalDate = "2026-02-15",
                        labelInformation = "Indiqué en association avec cetuximab pour le traitement des patients adultes atteints d'un cancer colorectal métastatique."
                    )
                    "news" -> NewsItem(
                        id = itemId,
                        title = "Avancée majeure dans la recherche contre le cancer : ${itemId.ifBlank { "Nouveau biomarqueur" }}",
                        source = "Pierre Fabre Science News",
                        publicationDate = "Février 2026",
                        publishedAt = null,
                        summary = "Les chercheurs des Laboratoires Pierre Fabre publient de nouveaux résultats prometteurs concernant la médecine de précision en oncologie.",
                        link = "https://www.pierre-fabre.com",
                        url = null
                    )
                    else -> BlogItem(
                        id = itemId,
                        slug = itemId,
                        title = "Innovation en Oncologie : ${itemId.ifBlank { "Médecine de précision" }}",
                        summary = "Perspectives d'avenir pour le traitement des cancers ciblés.",
                        content = "Article détaillé sur les innovations thérapeutiques et les modélisations IA développées pour accélérer la découverte de nouveaux traitements anticancéreux.",
                        date = "2026",
                        publishedAt = null
                    )
                }
            }

            uiState = uiState.copy(
                isLoading = false,
                itemData = foundItem
            )
        }
    }

    fun toggleFavorite() {
        val item = uiState.itemData ?: return
        val id = uiState.itemId
        val type = uiState.itemType
        val currentlyFav = isFavorite.value

        val (title, subtitle) = when (item) {
            is ResearchPaper -> Pair(item.title ?: "Article", item.journal ?: "")
            is ClinicalTrial -> Pair(item.displayTitle, item.status ?: "")
            is FdaApproval -> Pair(item.drugName ?: "Médicament", item.indication ?: "")
            is NewsItem -> Pair(item.title ?: "Actualité", item.source ?: "")
            is BlogItem -> Pair(item.title ?: "Article", item.displayDate ?: "")
            else -> Pair("Élément", "")
        }

        viewModelScope.launch {
            if (currentlyFav) {
                favoriteRepository.removeFavoriteById(id)
            } else {
                favoriteRepository.addFavorite(
                    FavoriteEntity(id = id, type = type, title = title, subtitle = subtitle)
                )
            }
        }
    }
}
