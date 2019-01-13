package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.impl.exception.SampleListFailure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository

class SampleListViewDataSource constructor(private val repository: SampleListRepository) : PositionalDataSource<SampleListItemView>() {

    var result = MutableLiveData<DataSourceResult>()

    sealed class DataSourceResult {
        object SuccessRefresh : DataSourceResult()
        object SuccessLoadMore : DataSourceResult()
        data class FailureRefresh(val failure: Failure) : DataSourceResult()
        data class FailureLoadMore(val failure: Failure) : DataSourceResult()
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<SampleListItemView>) {
        val response = repository.list(params.startPosition, params.loadSize)

        response.either(
                { failure ->
                    when (failure) {
                        is SampleListFailure.DataReadFailure -> {
                            result.postValue(DataSourceResult.SuccessLoadMore)
                            callback.onResult(emptyList())
                        }
                        else -> {
                            result.postValue(DataSourceResult.FailureLoadMore(failure))
                        }
                    }
                },
                { list ->
                    result.postValue(DataSourceResult.SuccessLoadMore)
                    callback.onResult(list.map { SampleListItemView(it.name) })

                }
        )
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<SampleListItemView>) {

        // HACK params.requestedStartPositionは使用しない
        // なぜかというと、
        // 仮に100個のデータが全データだった場合、100個の読み込みが完了していない状態で、datasource#invalidateを実行すると
        // params.requestedStartPositionの値が、その次のデータの読み込み位置が返却されるため。
        val response = repository.list(0, params.requestedLoadSize)
        response.either(
                { failure ->
                    when (failure) {
                        is SampleListFailure.DataReadFailure -> {
                            result.postValue(DataSourceResult.SuccessRefresh)
                            callback.onResult(emptyList(),0)
                        }
                        else -> {
                            result.postValue(DataSourceResult.FailureRefresh(failure))
                        }
                    }
                },
                { list ->
                    result.postValue(DataSourceResult.SuccessRefresh)
                    callback.onResult(list.map { SampleListItemView(it.name) }, 0)
                }
        )
    }
}