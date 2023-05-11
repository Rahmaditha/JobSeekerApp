package com.cookiss.jobseekerapp.data.remote

import com.cookiss.jobseekerapp.domain.model.JobsResponse
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/recruitment/positions.json")
    suspend fun getAllJobs(
        @Query("page") page: Int,
    ): JobsResponse

    @GET("/api/recruitment/positions/{id}")
    suspend fun getDetailJob(
        @Path("id") id: String
    ): JobsResponseItem

    @GET("/api/recruitment/positions.json")
    suspend fun searchJob(
        @Query("description") description: String?,
        @Query("location") location: String?,
        @Query("fulltime") fulltime: Boolean?,
    ): JobsResponse

}