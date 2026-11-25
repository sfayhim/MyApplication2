package com.example.myapplication.data.api

import com.example.myapplication.data.model.ApiResponse
import com.example.myapplication.data.model.BlogItem
import com.example.myapplication.data.model.ClinicalTrial
import com.example.myapplication.data.model.ClinToxRequest
import com.example.myapplication.data.model.ClinToxResponse
import com.example.myapplication.data.model.DtiRequest
import com.example.myapplication.data.model.DtiResponse
import com.example.myapplication.data.model.FdaApproval
import com.example.myapplication.data.model.NewsItem
import com.example.myapplication.data.model.PharmaSearchRequest
import com.example.myapplication.data.model.PharmaSearchResponse
import com.example.myapplication.data.model.PpiRequest
import com.example.myapplication.data.model.PpiResponse
import com.example.myapplication.data.model.ResearchPaper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject

class CureCancerApiService(
    private val client: HttpClient = KtorClient.httpClient
) {

    suspend fun getResearch(
        page: Int? = null,
        limit: Int? = null
    ): ApiResponse<ResearchPaper> = client.get("api/v1/research") {
        page?.let { parameter("page", it) }
        limit?.let { parameter("limit", it) }
    }.body()

    suspend fun getResearchDetail(idOrPubmedId: String): ResearchPaper =
        client.get("api/v1/research/$idOrPubmedId").body()

    suspend fun getNews(
        page: Int? = null,
        limit: Int? = null
    ): ApiResponse<NewsItem> = client.get("api/v1/news") {
        page?.let { parameter("page", it) }
        limit?.let { parameter("limit", it) }
    }.body()

    suspend fun getBlog(
        page: Int? = null,
        limit: Int? = null
    ): ApiResponse<BlogItem> = client.get("api/v1/blog") {
        page?.let { parameter("page", it) }
        limit?.let { parameter("limit", it) }
    }.body()

    suspend fun getBlogDetail(slug: String): BlogItem =
        client.get("api/v1/blog/$slug").body()

    suspend fun getFdaApprovals(
        page: Int? = null,
        limit: Int? = null
    ): ApiResponse<FdaApproval> = client.get("api/v1/fda-approvals") {
        page?.let { parameter("page", it) }
        limit?.let { parameter("limit", it) }
    }.body()

    suspend fun getClinicalTrials(
        page: Int? = null,
        limit: Int? = null
    ): ApiResponse<ClinicalTrial> = client.get("api/v1/clinical-trials") {
        page?.let { parameter("page", it) }
        limit?.let { parameter("limit", it) }
    }.body()

    suspend fun getClinicalTrialDetail(nctId: String): ClinicalTrial =
        client.get("api/v1/clinical-trials/$nctId").body()

    suspend fun search(
        query: String,
        types: String? = null
    ): JsonObject = client.get("api/v1/search") {
        parameter("q", query)
        types?.let { parameter("types", it) }
    }.body()

    // IBM MAMMAL Prediction Endpoints
    suspend fun predictPpi(request: PpiRequest): PpiResponse =
        client.post("api/v1/mammal/ppi") {
            setBody(request)
        }.body()

    suspend fun predictDti(request: DtiRequest): DtiResponse =
        client.post("api/v1/mammal/dti") {
            setBody(request)
        }.body()

    suspend fun predictClinTox(request: ClinToxRequest): ClinToxResponse =
        client.post("api/v1/mammal/clintox") {
            setBody(request)
        }.body()

    // Pharma Endpoints
    suspend fun searchPharma(request: PharmaSearchRequest): PharmaSearchResponse =
        client.post("api/v1/pharma/search") {
            setBody(request)
        }.body()

    suspend fun getPharmaCharacteristics(): JsonObject =
        client.get("api/v1/pharma/characteristics").body()
}
