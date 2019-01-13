package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.paging.PositionalDataSource
import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class AppPositionalDataSource<T>
constructor(
        val loadInit: (offset: Int, limit: Int) -> Either<Failure, List<T>>,
        val loadMore: (offset: Int, limit: Int) -> Either<Failure, List<T>>) : PositionalDataSource<T>() {

    private var retry: (() -> Any)? = null

    private fun handleRetry(retry:(() -> Any)){
        this.retry = retry
    }

    fun retryIfNeed(){
        val retry = this.retry
        this.retry = null
        retry?.let {
            CoroutineScope(Dispatchers.Default).async {
                it()
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        val result = loadMore(params.startPosition, params.loadSize)
        result.either(
                {
                    handleRetry { loadRange(params, callback) }
                },
                {
                    callback.onResult(it)
                }
        )
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {

        val result = loadInit(0, params.requestedLoadSize)
        result.either(
                {
                    handleRetry { loadInitial(params, callback) }
                },
                {
                    callback.onResult(it, 0)
                }
        )
    }
    enum class DataSourceState {
        NONE,
        Init,
        InitFailed,
        InitSuccess,
        More,
        MoreSuccess,
        MoreFailed

    }

}