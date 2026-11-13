package com.example.myapplication.ui.detail

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.BlogItem
import com.example.myapplication.data.model.ClinicalTrial
import com.example.myapplication.data.model.FdaApproval
import com.example.myapplication.data.model.NewsItem
import com.example.myapplication.data.model.ResearchPaper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemType: String,
    itemId: String,
    viewModel: DetailViewModel = viewModel(),
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(itemType, itemId) {
        viewModel.loadDetail(itemType, itemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de l'élément", fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                            onClick = { viewModel.loadDetail(itemType, itemId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Réessayer")
                        }
                    }
                }
            } else {
                val item = state.itemData
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // AI Disclaimer Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Prédiction IA — usage informatif uniquement. Cet outil ne constitue pas un diagnostic médical ni une recommandation clinique.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            when (item) {
                                is ResearchPaper -> {
                                    DetailHeader(type = "Recherche scientifique", title = item.title)
                                    DetailRow(label = "ID / PubMed ID", value = item.pubMedId ?: item.id ?: "N/A")
                                    if (!item.authors.isNullOrEmpty()) {
                                        DetailRow(label = "Auteurs", value = item.authors.joinToString(", "))
                                    }
                                    DetailRow(label = "Revue", value = item.journal ?: "N/A")
                                    DetailRow(label = "Date de publication", value = item.displayDate ?: "N/A")
                                    if (!item.abstractText.isNullOrEmpty()) {
                                        DetailSection(title = "Résumé / Abstract", content = item.abstractText)
                                    } else if (!item.summary.isNullOrEmpty()) {
                                        DetailSection(title = "Résumé", content = item.summary)
                                    }
                                }
                                is ClinicalTrial -> {
                                    DetailHeader(type = "Essai clinique", title = item.displayTitle)
                                    DetailRow(label = "NCT ID", value = item.nctId ?: "N/A")
                                    DetailRow(label = "Statut", value = item.status ?: "N/A")
                                    DetailRow(label = "Condition", value = item.condition ?: "N/A")
                                    DetailRow(label = "Intervention", value = item.intervention ?: "N/A")
                                    if (!item.eligibilityInformation.isNullOrEmpty()) {
                                        DetailSection(title = "Critères d'éligibilité", content = item.eligibilityInformation)
                                    }
                                    if (!item.locations.isNullOrEmpty()) {
                                        DetailSection(title = "Centres / Localisations", content = item.locations.joinToString("\n"))
                                    }
                                }
                                is FdaApproval -> {
                                    DetailHeader(type = "Approbation FDA", title = item.drugName)
                                    DetailRow(label = "Nom générique", value = item.genericName ?: "N/A")
                                    DetailRow(label = "Indication", value = item.indication ?: "N/A")
                                    DetailRow(label = "Entreprise", value = item.company ?: "N/A")
                                    DetailRow(label = "Date d'approbation", value = item.approvalDate ?: "N/A")
                                    if (!item.labelInformation.isNullOrEmpty()) {
                                        DetailSection(title = "Informations d'étiquetage", content = item.labelInformation)
                                    }
                                }
                                is NewsItem -> {
                                    DetailHeader(type = "Actualité", title = item.title)
                                    DetailRow(label = "Source", value = item.source ?: "N/A")
                                    DetailRow(label = "Date", value = item.displayDate ?: "N/A")
                                    if (!item.summary.isNullOrEmpty()) {
                                        DetailSection(title = "Résumé", content = item.summary)
                                    }
                                    if (!item.displayUrl.isNullOrEmpty()) {
                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, item.displayUrl!!.toUri())
                                                context.startActivity(intent)
                                            },
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Ouvrir la source originale")
                                        }
                                    }
                                }
                                is BlogItem -> {
                                    DetailHeader(type = "Article / Blog", title = item.title)
                                    DetailRow(label = "Date", value = item.displayDate ?: "N/A")
                                    if (!item.summary.isNullOrEmpty()) {
                                        DetailSection(title = "Résumé", content = item.summary)
                                    }
                                    if (!item.content.isNullOrEmpty()) {
                                        DetailSection(title = "Contenu", content = item.content)
                                    }
                                }
                                else -> {
                                    Text("Aucune information détaillée disponible.")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun DetailHeader(type: String, title: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CorporateBadge(text = type)
        Text(
            text = title ?: "Sans titre",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
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

@Composable
fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
