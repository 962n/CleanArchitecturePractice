package com.example.a962n.cleanarchitecturepractice.dagger.samplelist

import com.example.a962n.cleanarchitecturepractice.presentation.SampleListActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface SampleListActivityComponent : AndroidInjector<SampleListActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SampleListActivity>(){
//        abstract fun sampleListActivityModule(module: SampleListActivityModule) : Builder
    }
}