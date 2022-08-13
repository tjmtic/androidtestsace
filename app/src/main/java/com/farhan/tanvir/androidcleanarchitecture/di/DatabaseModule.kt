package com.farhan.tanvir.androidcleanarchitecture.di

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import com.farhan.tanvir.data.db.UserDB
import com.farhan.tanvir.data.db.UserDao
import com.farhan.tanvir.data.db.UserRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(app: Application): UserDB =
        Room.databaseBuilder(app, UserDB::class.java, "user_db").fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(userDB: UserDB) : UserDao = userDB.userDao()

    @Provides
    fun provideUserRemoteKeysDao(userDB: UserDB) : UserRemoteKeysDao = userDB.userRemoteKeysDao()
}