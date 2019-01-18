package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.example.a962n.cleanarchitecturepractice.util.PendingLiveData
import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.map
import com.example.a962n.cleanarchitecturepractice.domain.impl.GetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.observe
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.AppPositionalDataSource.DataSourceListener
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.AppPositionalDataSourceFactory
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.DataSourceState

class SampleListViewModel constructor(private val useCases: SampleListUseCases) : ViewModel() {

    private var failure: PendingLiveData<Failure> = PendingLiveData()
    private var success: PendingLiveData<Success> = PendingLiveData()

    var pagedList: LiveData<PagedList<SampleListItemView>>
    var dataSourceState: MutableLiveData<DataSourceState> = MutableLiveData()
    private var factory: AppPositionalDataSourceFactory<SampleListItemView>

    private val dataSourceListener: DataSourceListener<SampleListItemView> = object : DataSourceListener<SampleListItemView> {
        override fun loadInit(offset: Int, loadSize: Int): Either<Failure, List<SampleListItemView>> {
            return getSampleList(offset, loadSize)
        }

        override fun loadMore(offset: Int, loadSize: Int): Either<Failure, List<SampleListItemView>> {
            return getSampleList(offset, loadSize)
        }

        override fun onStateChange(state: DataSourceState) {
            when(state) {
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

    private fun getSampleList(offset: Int, loadSize: Int): Either<Failure, List<SampleListItemView>> {
        val result = useCases.getSampleList(GetSampleList.Param(offset, loadSize))
        return result.map { list -> list.map { SampleListItemView(it.name) } }
    }


    init {
        factory = AppPositionalDataSourceFactory(dataSourceListener)
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