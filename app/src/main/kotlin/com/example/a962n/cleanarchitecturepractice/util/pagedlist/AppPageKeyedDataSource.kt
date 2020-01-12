package com.example.a962n.cleanarchitecturepractice.util.pagedlist

import android.arch.paging.PageKeyedDataSource
import coreComponent.kotlin.Either
import com.example.a962n.domain.exception.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AppPageKeyedDataSource<K , T >
constructor(private val listener: DataSourceListener<K, T>) : PageKeyedDataSource<K, T>() {

    data class PageInfo<K , T > constructor(val prevKey: K?, val nextKey: K?, val list: List<T>)
    data class LoadParam<K> constructor(val requestSize: Int, val key: K)

    abstract class DataSourceListener<K , T > {

        abstract fun loadInit(requestSize: Int
                              , onResult: ((Either<Failure, PageInfo<K, T>>) -> Unit))

        open fun loadAfter(param: LoadParam<K>, onResult: ((Either<Failure, PageInfo<K, T>>) -> Unit)) {
            //please override and write fetch data source code.
        }

        open fun onStateChange(state: DataSourceState) {

        }
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

    override fun loadInitial(params: LoadInitialParams<K>, callback: LoadInitialCallback<K, T>) {
        postState(DataSourceState.LoadingInit)
        listener.loadInit(params.requestedLoadSize) { result ->
            result.fold({ failure ->
                handleRetry { loadInitial(params, callback) }
                postState(DataSourceState.LoadInitFailed(failure))
            }, { pageInfo ->
                callback.onResult(pageInfo.list, pageInfo.prevKey, pageInfo.nextKey)
                postState(DataSourceState.Loaded)
            })
        }
    }

    override fun loadAfter(params: LoadParams<K>, callback: LoadCallback<K, T>) {
        postState(DataSourceState.LoadingMore)
        listener.loadAfter(LoadParam(params.requestedLoadSize, params.key)) { result ->
            result.fold({ failure ->
                handleRetry { loadAfter(params, callback) }
                postState(DataSourceState.LoadMoreFailed(failure))
            }, { pageInfo ->
                callback.onResult(pageInfo.list, pageInfo.nextKey)
                postState(DataSourceState.Loaded)
            })
        }

    }

    override fun loadBefore(params: LoadParams<K>, callback: LoadCallback<K, T>) {


    }

    private fun postState(state: DataSourceState) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onStateChange(state)
        }
    }

}