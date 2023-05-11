package com.cookiss.jobseekerapp.data.remote

import androidx.paging.PagingSource
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem
import com.cookiss.jobseekerapp.util.Constants
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobsPagingSource @Inject constructor(
    private val apiService: ApiService
) : PagingSource<Int, JobsResponseItem>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, JobsResponseItem> {

        val position = params.key ?: Constants.STARTING_PAGE_INDEX


        return try {
            val response = apiService.getAllJobs(position)
            val listing = response

            val nextKey = if (listing.isEmpty()) {
                null
            }else{
                position + 1
            }

            val prevKey = if (position == Constants.STARTING_PAGE_INDEX) {
                null
            }else{
                position - 1
            }

            LoadResult.Page(
                data = listing,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (exception: IOException){
            return LoadResult.Error(exception)
        } catch (exception: HttpException){
            return LoadResult.Error(exception)
        }
    }
}