package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationMeta(
    @SerialName("total") val total: Int? = null,
    @SerialName("limit") val limit: Int? = null,
    @SerialName("offset") val offset: Int? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("totalPages") val totalPages: Int? = null
)

@Serializable
data class ApiResponse<T>(
    @SerialName("data") val data: List<T>? = null,
    @SerialName("items") val items: List<T>? = null,
    @SerialName("pagination") val pagination: PaginationMeta? = null,
    @SerialName("total") val total: Int? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("limit") val limit: Int? = null
) {
    val resultList: List<T>
        get() = data ?: items ?: emptyList()
}

@Serializable
data class ResearchPaper(
    @SerialName("id") val id: String? = null,
    @SerialName("pubmedId") val pubMedId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("authors") val authors: List<String>? = null,
    @SerialName("journal") val journal: String? = null,
    @SerialName("publicationDate") val publicationDate: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null,
    @SerialName("summary") val summary: String? = null,
    @SerialName("summaryPlain") val summaryPlain: String? = null,
    @SerialName("abstract") val abstractText: String? = null,
    @SerialName("cancerTypes") val cancerTypes: List<String>? = null,
    @SerialName("fullTextUrl") val fullTextUrl: String? = null
) {
    val displayDate: String? get() = publicationDate ?: publishedAt
    val displaySummary: String? get() = summary ?: summaryPlain
}

@Serializable
data class ClinicalTrial(
    @SerialName("nctId") val nctId: String? = null,
    @SerialName("trialTitle") val trialTitle: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("condition") val condition: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("intervention") val intervention: String? = null,
    @SerialName("conditions") val conditions: List<String>? = null,
    @SerialName("interventions") val interventions: List<String>? = null,
    @SerialName("eligibilityInformation") val eligibilityInformation: String? = null,
    @SerialName("locations") val locations: List<String>? = null
) {
    val displayTitle: String get() = trialTitle ?: title ?: condition ?: "Essai clinique"
}

@Serializable
data class FdaApproval(
    @SerialName("id") val id: String? = null,
    @SerialName("applicationNumber") val applicationNumber: String? = null,
    @SerialName("drugName") val drugName: String? = null,
    @SerialName("genericName") val genericName: String? = null,
    @SerialName("indication") val indication: String? = null,
    @SerialName("company") val company: String? = null,
    @SerialName("approvalDate") val approvalDate: String? = null,
    @SerialName("labelInformation") val labelInformation: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("labelPdfUrl") val labelPdfUrl: String? = null
)

@Serializable
data class NewsItem(
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("source") val source: String? = null,
    @SerialName("publicationDate") val publicationDate: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null,
    @SerialName("summary") val summary: String? = null,
    @SerialName("link") val link: String? = null,
    @SerialName("url") val url: String? = null
) {
    val displayDate: String? get() = publicationDate ?: publishedAt
    val displayUrl: String? get() = link ?: url
}

@Serializable
data class BlogItem(
    @SerialName("id") val id: String? = null,
    @SerialName("slug") val slug: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("summary") val summary: String? = null,
    @SerialName("excerpt") val excerpt: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null
) {
    val displayDate: String? get() = date ?: publishedAt
    val displaySummary: String? get() = summary ?: excerpt
}

// IBM MAMMAL & Pharma API Models
@Serializable
data class PpiRequest(
    @SerialName("protein_a") val proteinA: String,
    @SerialName("protein_b") val proteinB: String
)

@Serializable
data class PpiResponseData(
    @SerialName("prediction") val prediction: String? = null,
    @SerialName("label") val label: String? = null
)

@Serializable
data class PpiResponse(
    @SerialName("data") val data: PpiResponseData? = null
)

@Serializable
data class DtiRequest(
    @SerialName("target_seq") val targetSeq: String,
    @SerialName("drug_seq") val drugSeq: String,
    @SerialName("norm_y_mean") val normYMean: Double? = null,
    @SerialName("norm_y_std") val normYStd: Double? = null
)

@Serializable
data class DtiResponseData(
    @SerialName("pKd") val pKd: Double? = null
)

@Serializable
data class DtiResponse(
    @SerialName("data") val data: DtiResponseData? = null
)

@Serializable
data class ClinToxRequest(
    @SerialName("smiles") val smiles: String
)

@Serializable
data class ClinToxResponseData(
    @SerialName("pred") val pred: Int? = null,
    @SerialName("score") val score: Double? = null
)

@Serializable
data class ClinToxResponse(
    @SerialName("data") val data: ClinToxResponseData? = null
)

@Serializable
data class PharmaSearchRequest(
    @SerialName("examples") val examples: List<String>? = null,
    @SerialName("preferences") val preferences: Map<String, Int>? = null,
    @SerialName("limit") val limit: Int? = 20,
    @SerialName("include_characteristics") val includeCharacteristics: Boolean? = true
)

@Serializable
data class PharmaCompound(
    @SerialName("rank") val rank: Int? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("score") val score: Double? = null,
    @SerialName("characteristics") val characteristics: Map<String, Double>? = null
)

@Serializable
data class PharmaSearchData(
    @SerialName("domain") val domain: String? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("recommendations") val recommendations: List<PharmaCompound>? = null
)

@Serializable
data class PharmaSearchResponse(
    @SerialName("data") val data: PharmaSearchData? = null
)
