package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.example.a962n.cleanarchitecturepractice.PendingLiveData
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository
import com.example.a962n.cleanarchitecturepractice.domain.impl.GetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.observe

class SampleListViewModel constructor(private val useCases: SampleListUseCases,private val repository: SampleListRepository) : ViewModel() {

    private var failure: PendingLiveData<Failure> = PendingLiveData()
    private var success: PendingLiveData<Success> = PendingLiveData()
    var pagedList:MutableLiveData<PagedList<SampleListItemView>> = MutableLiveData()
    private var dataSource:SampleListViewDataSource

    var list: MutableLiveData<MutableList<SampleListItemView>> = MutableLiveData()

    init {
        list.value = mutableListOf()
        dataSource = SampleListViewDataSource(repository)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .setInitialLoadSizeHint(10)
                .build()
        pagedList.value = PagedList.Builder(dataSource,config).build()
    }

    fun refresh() {
        dataSource.invalidate()
//        useCases.getSampleList(GetSampleList.Param.Refresh) {
//            it.either(::handleFailure) { right ->
//                success.value = Success.Refresh
//                list.value?.clear()
//                handleList(right)
//            }
//        }
    }

    fun loadMore() {
        useCases.getSampleList(GetSampleList.Param.LoadMore) {
            it.either(::handleFailure) { list ->
                success.value = Success.LoadMore
                handleList(list)
            }
        }
    }


    private fun handleList(sampleList: List<SampleListEntity>) {
        list.value?.let { list ->
            list.addAll(sampleList.map { SampleListItemView(it.name) })
        }
        list.value = list.value
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

    sealed class Success {
        object Refresh : Success()
        object LoadMore : Success()
    }

}