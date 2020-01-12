package com.example.a962n.cleanarchitecturepractice.util.pagedlist

import com.example.a962n.domain.exception.Failure

sealed class DataSourceState {
    object Loaded : DataSourceState()
    object LoadingInit : DataSourceState()
    object LoadingMore : DataSourceState()
    data class LoadInitFailed(val reason: Failure) : DataSourceState()
    data class LoadMoreFailed(val reason: Failure) : DataSourceState()
}
