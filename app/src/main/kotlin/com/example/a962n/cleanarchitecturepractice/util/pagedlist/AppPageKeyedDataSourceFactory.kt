package com.example.a962n.cleanarchitecturepractice.util.pagedlist

import androidx.paging.DataSource

class AppPageKeyedDataSourceFactory<K, T>
constructor(private val listener: AppPageKeyedDataSource.DataSourceListener<K, T>) : DataSource.Factory<K, T>() {

    private var nowDataSource: AppPageKeyedDataSource<K, T>? = null

    fun invalidate() {
        nowDataSource?.invalidate()
    }

    fun retryIfNeed() {
        nowDataSource?.retryIfNeed()
    }

    override fun create(): DataSource<K, T> {
        //HACK
        //データの再読み込みなどをする場合にdataSource#invalidateを実施するが
        //invalidateを実行するとFactory#createが再度実行され、DataSourceが再生成を求めるので本メソッドでは
        //必ず生成処理を行う(DataSourceの使い回しはしてはいけない)
        val dataSource = AppPageKeyedDataSource(listener)
        nowDataSource = dataSource
        return dataSource
    }
}

