package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.domain.impl.GetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.observe

class SampleListViewModel constructor(private val useCases: SampleListUseCases) : ViewModel() {

    private var failure: MutableLiveData<Failure> = MutableLiveData()
    private var success: MutableLiveData<Success> = MutableLiveData()

    var list: MutableLiveData<MutableList<SampleListItemView>> = MutableLiveData()

    init {
        list.value = mutableListOf()
    }

    fun refresh() {
        useCases.getSampleList(GetSampleList.Param.Refresh) {
            it.either(::handleFailure) { right ->
                success.value = Success.Refresh
                list.value?.clear()
                handleList(right)
            }
        }
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