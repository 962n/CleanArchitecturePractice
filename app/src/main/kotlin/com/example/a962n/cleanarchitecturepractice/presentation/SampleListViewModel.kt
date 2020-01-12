package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.example.a962n.cleanarchitecturepractice.util.PendingLiveData
import coreComponent.kotlin.Either
import com.example.a962n.domain.exception.Failure
import com.example.a962n.domain.useCase.sample.AsyncGetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.observe
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.AppPageKeyedDataSource
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.AppPageKeyedDataSourceFactory
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.DataSourceState

class SampleListViewModel constructor(private val useCases: SampleListUseCases) : ViewModel() {

    private var failure: PendingLiveData<Failure> = PendingLiveData()
    private var success: PendingLiveData<Success> = PendingLiveData()

    var pagedList: LiveData<PagedList<SampleListItemView>>
    var dataSourceState: MutableLiveData<DataSourceState> = MutableLiveData()
    private var factory: AppPageKeyedDataSourceFactory<String,SampleListItemView>

    private val pageKeyDataSourceListener: AppPageKeyedDataSource.DataSourceListener<String, SampleListItemView> = object : AppPageKeyedDataSource.DataSourceListener<String, SampleListItemView>() {
        override fun loadInit(requestSize: Int, onResult: (Either<Failure, AppPageKeyedDataSource.PageInfo<String, SampleListItemView>>) -> Unit) {
            useCases.getSampleList(AsyncGetSampleList.Param(0, requestSize)) { result ->
                result.fold({ failure ->
                    onResult(Either.Left(failure))
                }, { list ->
                    val mapList = list.map { SampleListItemView(it.name) }
                    val prevKey: String? = null
                    val nextKey: String = list.size.toString()
                    var info = AppPageKeyedDataSource.PageInfo(prevKey, nextKey, mapList)
                    onResult(Either.Right(info))
                })
            }

        }

        override fun loadAfter(param: AppPageKeyedDataSource.LoadParam<String>, onResult: (Either<Failure, AppPageKeyedDataSource.PageInfo<String, SampleListItemView>>) -> Unit) {
            val offset = param.key.toInt()
            useCases.getSampleList(AsyncGetSampleList.Param(offset, param.requestSize)) { result ->
                result.fold({ failure ->
                    onResult(Either.Left(failure))
                }, { list ->
                    val mapList = list.map { SampleListItemView(it.name) }
                    val prevKey: String? = null
                    val nextKey: String = (list.size + offset).toString()
                    var info = AppPageKeyedDataSource.PageInfo(prevKey, nextKey, mapList)
                    onResult(Either.Right(info))
                })
            }

        }

        override fun onStateChange(state: DataSourceState) {
            when (state) {
                is DataSourceState.LoadInitFailed -> {
                    // HACK 初回データ読み込みの場合はユーザーへ
                    // なにがしかエラー通知(dialog or toast)する必要があるため、処理結果をUIヘ通知する
                    handleFailure(state.reason)
                }
                else -> {
                    //do nothing
                }
            }
            dataSourceState.value = state
        }
    }
//    private val dataSourceListener: DataSourceListener<SampleListItemView> = object : DataSourceListener<SampleListItemView> {
//        override fun loadInit(offset: Int, loadSize: Int): Either<Failure, List<SampleListItemView>> {
//            return getSampleList(offset, loadSize)
//        }
//
//        override fun loadMore(offset: Int, loadSize: Int): Either<Failure, List<SampleListItemView>> {
//            return getSampleList(offset, loadSize)
//        }
//
//        override fun onStateChange(state: DataSourceState) {
//            when (state) {
//                is DataSourceState.LoadInitFailed -> {
//                    // HACK 初回データ読み込みの場合はユーザーへ
//                    // なにがしかエラー通知(dialog or toast)する必要があるため、処理結果をUIヘ通知する
//                    handleFailure(state.reason)
//                }
//                else -> {
//                    //do nothing
//                }
//            }
//            dataSourceState.value = state
//        }
//    }
//
//    private fun getSampleList(offset: Int, loadSize: Int): Either<Failure, List<SampleListItemView>> {
//        val result = useCases.getSampleList(GetSampleList.Param(offset, loadSize))
//        return result.map { list -> list.map { SampleListItemView(it.name) } }
//    }


    init {
        factory = AppPageKeyedDataSourceFactory(pageKeyDataSourceListener)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build()
        pagedList = LivePagedListBuilder(factory, config).build()
    }

    fun refresh() {
        factory.invalidate()
    }

    fun retry() {
        factory.retryIfNeed()
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

    sealed class Success {
        object Refresh : Success()
        object LoadMore : Success()
    }

}