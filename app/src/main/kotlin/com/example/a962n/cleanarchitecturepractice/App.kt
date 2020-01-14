package com.example.a962n.cleanarchitecturepractice

import android.app.Activity
import android.app.Application
import com.example.a962n.cleanarchitecturepractice.dagger.AppModule
import com.example.a962n.cleanarchitecturepractice.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject
import dagger.android.DispatchingAndroidInjector


class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>


    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }

    override fun onCreate() {
        super.onCreate()
        val builder = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
        builder.seedInstance(this)
        builder.build().inject(this)

    }
}