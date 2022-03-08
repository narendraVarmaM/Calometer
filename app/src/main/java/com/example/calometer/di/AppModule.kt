package com.example.calometer.di

import android.content.Context
import androidx.room.Room
import com.example.calometer.MyApplication
import com.example.calometer.database.CalometerDatabase
import com.example.calometer.repository.FoodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context)= Room.databaseBuilder(
        context,
        CalometerDatabase::class.java,
        "food_database"
    ).createFromAsset("database/food_database.db").build()

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context):MyApplication{
        return app as MyApplication
    }

    @Provides
    @Singleton
    fun foodRepository(database: CalometerDatabase)= FoodRepository(database.foodDataDao, database.userDataDao)
}