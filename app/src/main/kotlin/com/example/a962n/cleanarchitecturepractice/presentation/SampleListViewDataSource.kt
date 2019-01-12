package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.paging.PositionalDataSource
import android.util.Log
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository

class SampleListViewDataSource constructor(private val repository: SampleListRepository) : PositionalDataSource<SampleListItemView>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<SampleListItemView>) {
        Log.d("hoge","loadRange thread name = " + Thread.currentThread().name)
        val result = repository.list(params.startPosition,params.loadSize)
        result.either(
                {failure -> Log.d("hoge","loadRange失敗") },
                {list -> callback.onResult(list.map{SampleListItemView(it.name)}) }
        )
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<SampleListItemView>) {
        Log.d("hoge","loadInitial thread name = " + Thread.currentThread().name)

        val result = repository.list(params.requestedStartPosition,params.requestedLoadSize)
        result.either(
                {failure -> Log.d("hoge","loadInitial失敗") },
                {list -> callback.onResult(list.map{SampleListItemView(it.name)},0) }
        )
    }
}