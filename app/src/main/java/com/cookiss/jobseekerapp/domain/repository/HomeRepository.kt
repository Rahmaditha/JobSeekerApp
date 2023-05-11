package com.cookiss.jobseekerapp.domain.repository

import androidx.paging.PagingData
import com.cookiss.jobseekerapp.domain.model.JobsResponse
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun fetchJobs(): Flow<PagingData<JobsResponseItem>>

    fun getDetailJob(
        id: String,
    ) : Flow<JobsResponseItem>

    fun searchJob(
        description: String?,
        location: String?,
        fulltime: Boolean?,
    ) : Flow<JobsResponse>
}