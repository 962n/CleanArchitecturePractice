package com.example.a962n.cleanarchitecturepractice.dagger.samplelist


import com.example.a962n.cleanarchitecturepractice.data.impl.SampleListNetworkDummy
import com.example.a962n.domain.repository.SampleListRepository
import com.example.a962n.cleanarchitecturepractice.presentation.SampleListActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [SampleListActivityComponent::class])
abstract class SampleListActivityModule {
    @Binds
    @IntoMap
    @ClassKey(SampleListActivity::class)
    abstract fun bindSampleListActivityInjectorFactory(builder: SampleListActivityComponent.Builder): AndroidInjector.Factory<*>

    @Binds
    abstract fun provideSampleListRepository(dataSource: SampleListNetworkDummy): SampleListRepository

}