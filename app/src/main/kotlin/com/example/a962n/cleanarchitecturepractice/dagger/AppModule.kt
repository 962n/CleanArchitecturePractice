package com.example.a962n.cleanarchitecturepractice.dagger

import android.content.Context
import com.example.a962n.cleanarchitecturepractice.App
import com.example.a962n.data.NetworkHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: App) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application


    @Provides
    @Singleton
    fun provideNetworkHandler(): NetworkHandler = NetworkHandler(application)

}