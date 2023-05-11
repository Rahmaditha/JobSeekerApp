package com.cookiss.jobseekerapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cookiss.jobseekerapp.data.remote.ApiService
import com.cookiss.jobseekerapp.data.remote.JobsPagingSource
import com.cookiss.jobseekerapp.domain.model.JobsResponse
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem
import com.cookiss.jobseekerapp.domain.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val apiService : ApiService
): HomeRepository{
    override fun fetchJobs(): Flow<PagingData<JobsResponseItem>> {
        return Pager(
            PagingConfig(pageSize = 20, enablePlaceholders = false)
        ) {
            JobsPagingSource(apiService)
        }.flow
    }

    override fun getDetailJob(id: String): Flow<JobsResponseItem> {
        return flow {
            val jobDetailResult = apiService.getDetailJob(id)
            emit(jobDetailResult)
        }.flowOn(Dispatchers.IO)
    }

    override fun searchJob(
        description: String?,
        location: String?,
        fulltime: Boolean?
    ): Flow<JobsResponse> {
        return flow {
            val searchJobResult = apiService.searchJob(description, location, fulltime)
            emit(searchJobResult)
        }.flowOn(Dispatchers.IO)
    }


}