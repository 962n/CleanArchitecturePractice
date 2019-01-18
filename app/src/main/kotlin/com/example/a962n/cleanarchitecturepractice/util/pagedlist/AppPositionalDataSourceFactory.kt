package com.example.a962n.cleanarchitecturepractice.util.pagedlist

import android.arch.paging.DataSource
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.AppPositionalDataSource.DataSourceListener

class AppPositionalDataSourceFactory<T>
constructor(private val listener: DataSourceListener<T>) : DataSource.Factory<Int, T>() {

    private var nowDataSource: AppPositionalDataSource<T>? = null

    fun invalidate() {
        nowDataSource?.invalidate()
    }

    fun retryIfNeed() {
        nowDataSource?.retryIfNeed()
    }

    override fun create(): DataSource<Int, T> {
        //HACK
        //データの再読み込みなどをする場合にdataSource#invalidateを実施するが
        //invalidateを実行するとFactory#createが再度実行され、DataSourceが再生成を求めるので本メソッドでは
        //必ず生成処理を行う(DataSourceの使い回しはしてはいけない)
        val dataSource = AppPositionalDataSource(listener)
        nowDataSource = dataSource
        return dataSource
    }
}

