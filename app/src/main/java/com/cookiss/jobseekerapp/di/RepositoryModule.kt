package com.cookiss.jobseekerapp.di

import com.cookiss.jobseekerapp.data.remote.ApiService
import com.cookiss.jobseekerapp.data.repository.HomeRepositoryImpl
import com.cookiss.jobseekerapp.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesHomeRepository(
        apiService: ApiService
    ) : HomeRepository {
        return HomeRepositoryImpl(apiService)
    }

}