package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.*
import android.arch.lifecycle.Transformations.switchMap
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.example.a962n.cleanarchitecturepractice.PendingLiveData
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository
import com.example.a962n.cleanarchitecturepractice.domain.impl.GetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.observe
import com.example.a962n.cleanarchitecturepractice.presentation.SampleListViewDataSource.DataSourceResult

class SampleListViewModel constructor(private val useCases: SampleListUseCases, repository: SampleListRepository) : ViewModel() {

    private var failure: PendingLiveData<Failure> = PendingLiveData()
    private var success: PendingLiveData<Success> = PendingLiveData()


    var pagedList: LiveData<PagedList<SampleListItemView>>
    private var factory: SampleListViewDataSourceFactory = SampleListViewDataSourceFactory(repository)
    private var dataSourceObserver: Observer<DataSourceResult>
    private var dataSourceResult: LiveData<DataSourceResult>

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build()
        pagedList = LivePagedListBuilder(factory, config).build()

        dataSourceObserver = Observer { it ->
            //HACK dataSourceでの取得結果を検知したらViewModel用の結果通知に変換して、UI側へ通知する
            when (it) {
                is DataSourceResult.SuccessRefresh -> handleSuccess(Success.Refresh)
                is DataSourceResult.SuccessLoadMore -> handleSuccess(Success.LoadMore)
                is DataSourceResult.FailureLoadMore -> handleFailure(it.failure)
                is DataSourceResult.FailureRefresh -> handleFailure(it.failure)
            }
        }
        dataSourceResult = switchMap(factory.nowDataSource) { it.result }
        dataSourceResult.observeForever(dataSourceObserver)
    }

    fun refresh() {
        factory.nowDataSource.value?.invalidate()
    }

    private fun handleSuccess(success: Success) {
        this.success.value = success
    }

    private fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }

    fun failure(owner: LifecycleOwner, observer: (Failure?) -> Unit) {
        owner.observe(failure, observer)
    }

    fun success(owner: LifecycleOwner, observer: (Success?) -> Unit) {
        owner.observe(success, observer)
    }

    override fun onCleared() {
        dataSourceResult.removeObserver(dataSourceObserver)
        super.onCleared()
    }

    sealed class Success {
        object Refresh : Success()
        object LoadMore : Success()
    }

}