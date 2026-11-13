package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("data") val data: List<T>?,
    @SerializedName("items") val items: List<T>?,
    @SerializedName("total") val total: Int?,
    @SerializedName("page") val page: Int?,
    @SerializedName("limit") val limit: Int?
) {
    val resultList: List<T>
        get() = data ?: items ?: emptyList()
}

data class ResearchPaper(
    @SerializedName("id") val id: String?,
    @SerializedName("pubmedId") val pubMedId: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("journal") val journal: String?,
    @SerializedName("publicationDate") val publicationDate: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("abstract") val abstractText: String?
) {
    val displayDate: String? get() = publicationDate ?: publishedAt
}

data class ClinicalTrial(
    @SerializedName("nctId") val nctId: String?,
    @SerializedName("trialTitle") val trialTitle: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("condition") val condition: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("intervention") val intervention: String?,
    @SerializedName("conditions") val conditions: List<String>?,
    @SerializedName("interventions") val interventions: List<String>?,
    @SerializedName("eligibilityInformation") val eligibilityInformation: String?,
    @SerializedName("locations") val locations: List<String>?
) {
    val displayTitle: String get() = trialTitle ?: title ?: condition ?: "Essai clinique"
}

data class FdaApproval(
    @SerializedName("id") val id: String?,
    @SerializedName("drugName") val drugName: String?,
    @SerializedName("genericName") val genericName: String?,
    @SerializedName("indication") val indication: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("approvalDate") val approvalDate: String?,
    @SerializedName("labelInformation") val labelInformation: String?
)

data class NewsItem(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("publicationDate") val publicationDate: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("link") val link: String?,
    @SerializedName("url") val url: String?
) {
    val displayDate: String? get() = publicationDate ?: publishedAt
    val displayUrl: String? get() = link ?: url
}

data class BlogItem(
    @SerializedName("id") val id: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("publishedAt") val publishedAt: String?
) {
    val displayDate: String? get() = date ?: publishedAt
}

// IBM MAMMAL & Pharma API Models
data class PpiRequest(
    @SerializedName("protein_a") val proteinA: String,
    @SerializedName("protein_b") val proteinB: String
)

data class PpiResponseData(
    @SerializedName("prediction") val prediction: String?,
    @SerializedName("label") val label: String?
)

data class PpiResponse(
    @SerializedName("data") val data: PpiResponseData?
)

data class DtiRequest(
    @SerializedName("target_seq") val targetSeq: String,
    @SerializedName("drug_seq") val drugSeq: String,
    @SerializedName("norm_y_mean") val normYMean: Double? = null,
    @SerializedName("norm_y_std") val normYStd: Double? = null
)

data class DtiResponseData(
    @SerializedName("pKd") val pKd: Double?
)

data class DtiResponse(
    @SerializedName("data") val data: DtiResponseData?
)

data class ClinToxRequest(
    @SerializedName("smiles") val smiles: String
)

data class ClinToxResponseData(
    @SerializedName("pred") val pred: Int?,
    @SerializedName("score") val score: Double?
)

data class ClinToxResponse(
    @SerializedName("data") val data: ClinToxResponseData?
)

data class PharmaSearchRequest(
    @SerializedName("examples") val examples: List<String>? = null,
    @SerializedName("preferences") val preferences: Map<String, Int>? = null,
    @SerializedName("limit") val limit: Int? = 20,
    @SerializedName("include_characteristics") val includeCharacteristics: Boolean? = true
)

data class PharmaCompound(
    @SerializedName("rank") val rank: Int?,
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("characteristics") val characteristics: Map<String, Double>?
)

data class PharmaSearchData(
    @SerializedName("domain") val domain: String?,
    @SerializedName("count") val count: Int?,
    @SerializedName("recommendations") val recommendations: List<PharmaCompound>?
)

data class PharmaSearchResponse(
    @SerializedName("data") val data: PharmaSearchData?
)
