package com.tiphubapps.ax.rain.di

import com.tiphubapps.ax.data.repository.UserRepositoryImpl
import com.tiphubapps.ax.data.repository.dataSource.UserDataSource
import com.tiphubapps.ax.data.repository.dataSource.UserLocalDataSource
import com.tiphubapps.ax.data.repository.dataSource.UserRemoteDataSource
import com.tiphubapps.ax.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUsersRepository(
        @Named("remote") userRemoteDataSource: UserDataSource,
        @Named("local") userLocalDataSource: UserDataSource
    ): UserRepository =
        UserRepositoryImpl(userRemoteDataSource = userRemoteDataSource, userLocalDataSource = userLocalDataSource)
}