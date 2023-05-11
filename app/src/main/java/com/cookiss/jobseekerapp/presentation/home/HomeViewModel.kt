package com.cookiss.jobseekerapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cookiss.jobseekerapp.data.remote.Resource
import com.cookiss.jobseekerapp.domain.model.JobsResponse
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem
import com.cookiss.jobseekerapp.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel(){

    val TAG = this.javaClass.simpleName

    private val _jobResult = MutableLiveData<Resource<JobsResponseItem>>()
    val jobResult : LiveData<Resource<JobsResponseItem>> = _jobResult

    private val _searchJobResult = MutableLiveData<Resource<JobsResponse>>()
    val searchJobResult : LiveData<Resource<JobsResponse>> = _searchJobResult

    fun fetchJob(): Flow<PagingData<JobsResponseItem>> {
        return repository.fetchJobs().cachedIn(viewModelScope)
    }

    fun getDetailJob(id: String){
        viewModelScope.launch {
            repository.getDetailJob(id)
                .onStart {
                    _jobResult.postValue(Resource.Loading(true))
                }
                .catch {
                    it.message?.let { message ->
                        _jobResult.postValue(Resource.Error(message))
                    }
                }
                .collect { listMetodePembayaran ->
                    _jobResult.postValue(Resource.Success(listMetodePembayaran))
                }
        }
    }

    fun searchJob(description: String?, location: String?, fulltime: Boolean?){
        viewModelScope.launch {
            repository.searchJob(description, location, fulltime)
                .onStart {
                    _searchJobResult.postValue(Resource.Loading(true))
                }
                .catch {
                    it.message?.let { message ->
                        _searchJobResult.postValue(Resource.Error(message))
                    }
                }
                .collect { listMetodePembayaran ->
                    _searchJobResult.postValue(Resource.Success(listMetodePembayaran))
                }
        }
    }

}