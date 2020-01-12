package com.example.a962n.cleanarchitecturepractice.dagger

import com.example.a962n.cleanarchitecturepractice.App
import com.example.a962n.cleanarchitecturepractice.dagger.samplelist.SampleListActivityModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,AppModule::class, SampleListActivityModule::class])
interface AppComponent : AndroidInjector<App>{

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {
        abstract fun appModule(appModule: AppModule) : Builder
    }

}