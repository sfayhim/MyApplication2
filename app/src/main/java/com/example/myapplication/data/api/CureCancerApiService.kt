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
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CureCancerApiService {

    @GET("api/v1/research")
    suspend fun getResearch(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<ResearchPaper>

    @GET("api/v1/research/{idOrPubmedId}")
    suspend fun getResearchDetail(
        @Path("idOrPubmedId") idOrPubmedId: String
    ): ResearchPaper

    @GET("api/v1/news")
    suspend fun getNews(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<NewsItem>

    @GET("api/v1/blog")
    suspend fun getBlog(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<BlogItem>

    @GET("api/v1/blog/{slug}")
    suspend fun getBlogDetail(
        @Path("slug") slug: String
    ): BlogItem

    @GET("api/v1/fda-approvals")
    suspend fun getFdaApprovals(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<FdaApproval>

    @GET("api/v1/clinical-trials")
    suspend fun getClinicalTrials(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<ClinicalTrial>

    @GET("api/v1/clinical-trials/{nctId}")
    suspend fun getClinicalTrialDetail(
        @Path("nctId") nctId: String
    ): ClinicalTrial

    @GET("api/v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("types") types: String? = null
    ): JsonObject

    // IBM MAMMAL Prediction Endpoints
    @POST("api/v1/mammal/ppi")
    suspend fun predictPpi(
        @Body request: PpiRequest
    ): PpiResponse

    @POST("api/v1/mammal/dti")
    suspend fun predictDti(
        @Body request: DtiRequest
    ): DtiResponse

    @POST("api/v1/mammal/clintox")
    suspend fun predictClinTox(
        @Body request: ClinToxRequest
    ): ClinToxResponse

    // Pharma Endpoints
    @POST("api/v1/pharma/search")
    suspend fun searchPharma(
        @Body request: PharmaSearchRequest
    ): PharmaSearchResponse

    @GET("api/v1/pharma/characteristics")
    suspend fun getPharmaCharacteristics(): JsonObject
}
