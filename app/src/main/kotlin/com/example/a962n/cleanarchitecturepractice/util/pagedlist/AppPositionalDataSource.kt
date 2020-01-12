package com.example.a962n.cleanarchitecturepractice.util.pagedlist

import android.arch.paging.PositionalDataSource
import coreComponent.kotlin.Either
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AppPositionalDataSource<T>
constructor(private val listener: DataSourceListener<T>) : PositionalDataSource<T>() {

    interface DataSourceListener<V> {
        fun loadInit(offset: Int, loadSize: Int): Either<Failure, List<V>>
        fun loadMore(offset: Int, loadSize: Int): Either<Failure, List<V>>
        fun onStateChange(state: DataSourceState)
    }

    private var retry: (() -> Any)? = null


    private fun handleRetry(retry: (() -> Any)) {
        this.retry = retry
    }

    fun retryIfNeed() {
        val retry = this.retry
        this.retry = null
        retry?.let {
            CoroutineScope(Dispatchers.Default).async {
                it()
            }
        }
    }


    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        postState(DataSourceState.LoadingMore)
        val result = listener.loadMore(params.startPosition, params.loadSize)
        result.fold(
                {
                    handleRetry { loadRange(params, callback) }
                    postState(DataSourceState.LoadMoreFailed(it))
                },
                {
                    callback.onResult(it)
                    postState(DataSourceState.Loaded)
                }
        )
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        postState(DataSourceState.LoadingInit)
        val result = listener.loadInit(0, params.requestedLoadSize)
        result.fold(
                {
                    handleRetry { loadInitial(params, callback) }
                    postState(DataSourceState.LoadInitFailed(it))
                },
                {
                    callback.onResult(it, 0)
                    postState(DataSourceState.Loaded)
                }
        )
    }
    private fun postState(state: DataSourceState){
        CoroutineScope(Dispatchers.Main).launch {
            listener.onStateChange(state)
        }
    }
}