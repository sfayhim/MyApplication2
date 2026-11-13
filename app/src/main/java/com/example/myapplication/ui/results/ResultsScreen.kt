package com.example.myapplication.ui.results

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.BlogItem
import com.example.myapplication.data.model.ClinicalTrial
import com.example.myapplication.data.model.FdaApproval
import com.example.myapplication.data.model.NewsItem
import com.example.myapplication.data.model.ResearchPaper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    query: String,
    viewModel: ResultsViewModel = viewModel(),
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String, String) -> Unit
) {
    val state = viewModel.uiState
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columns = if (isLandscape) 2 else 1

    LaunchedEffect(query) {
        viewModel.loadResults(query)
    }

    val tabs = listOf("Tous", "Recherche", "Essais cliniques", "Approbations FDA", "Actualités")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Résultats scientifiques", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Requête : \"$query\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = tabs.indexOf(state.selectedTab).coerceAtLeast(0),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { viewModel.loadResults(query) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Réessayer")
                        }
                    }
                }
            } else {
                val filteredItems = mutableListOf<Any>()
                val res = state.searchResults

                when (state.selectedTab) {
                    "Tous" -> {
                        res["research"]?.let { filteredItems.addAll(it) }
                        res["clinicalTrials"]?.let { filteredItems.addAll(it) }
                        res["fdaApprovals"]?.let { filteredItems.addAll(it) }
                        res["news"]?.let { filteredItems.addAll(it) }
                        res["blog"]?.let { filteredItems.addAll(it) }
                    }
                    "Recherche" -> {
                        res["research"]?.let { filteredItems.addAll(it) }
                    }
                    "Essais cliniques" -> {
                        res["clinicalTrials"]?.let { filteredItems.addAll(it) }
                    }
                    "Approbations FDA" -> {
                        res["fdaApprovals"]?.let { filteredItems.addAll(it) }
                    }
                    "Actualités" -> {
                        res["news"]?.let { filteredItems.addAll(it) }
                        res["blog"]?.let { filteredItems.addAll(it) }
                    }
                }

                if (filteredItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucun résultat trouvé pour cette catégorie.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item(span = { GridItemSpan(columns) }) { Spacer(modifier = Modifier.height(4.dp)) }
                        items(filteredItems) { item ->
                            DetailedResultCard(item = item, onClick = {
                                when (item) {
                                    is ResearchPaper -> onNavigateToDetail("research", item.id ?: item.pubMedId ?: "")
                                    is ClinicalTrial -> onNavigateToDetail("clinicalTrials", item.nctId ?: "")
                                    is FdaApproval -> onNavigateToDetail("fdaApprovals", item.id ?: "")
                                    is NewsItem -> onNavigateToDetail("news", item.id ?: "")
                                    is BlogItem -> onNavigateToDetail("blog", item.slug ?: item.id ?: "")
                                }
                            })
                        }
                        item(span = { GridItemSpan(columns) }) { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedResultCard(
    item: Any,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (item) {
                is ResearchPaper -> {
                    CorporateBadge(text = "Recherche")
                    Text(
                        text = item.title ?: "Sans titre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!item.authors.isNullOrEmpty()) {
                        Text(
                            text = "Auteurs : ${item.authors.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.journal ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                        Text(text = item.displayDate ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!item.summary.isNullOrEmpty()) {
                        Text(
                            text = item.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                is ClinicalTrial -> {
                    CorporateBadge(text = "Essai clinique")
                    Text(
                        text = item.displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "NCT ID : ${item.nctId ?: "N/A"} | Statut : ${item.status ?: "Inconnu"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                    if (!item.condition.isNullOrEmpty()) {
                        Text(text = "Condition : ${item.condition}", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (!item.intervention.isNullOrEmpty()) {
                        Text(text = "Intervention : ${item.intervention}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                is FdaApproval -> {
                    CorporateBadge(text = "Approbation FDA")
                    Text(
                        text = item.drugName ?: "Médicament",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nom générique : ${item.genericName ?: "N/A"} | Entreprise : ${item.company ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (!item.indication.isNullOrEmpty()) {
                        Text(text = "Indication : ${item.indication}", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (!item.approvalDate.isNullOrEmpty()) {
                        Text(text = "Date d'approbation : ${item.approvalDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                is NewsItem -> {
                    CorporateBadge(text = "Actualité")
                    Text(
                        text = item.title ?: "Actualité",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.source ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                        Text(text = item.displayDate ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!item.summary.isNullOrEmpty()) {
                        Text(
                            text = item.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                is BlogItem -> {
                    CorporateBadge(text = "Article & Blog")
                    Text(
                        text = item.title ?: "Article",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.displayDate ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!item.summary.isNullOrEmpty()) {
                        Text(
                            text = item.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Prédiction IA — usage informatif uniquement",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun CorporateBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}
