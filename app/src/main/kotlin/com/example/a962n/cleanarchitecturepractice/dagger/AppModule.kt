package com.example.a962n.cleanarchitecturepractice.dagger

import android.content.Context
import com.example.a962n.cleanarchitecturepractice.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: App) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

}