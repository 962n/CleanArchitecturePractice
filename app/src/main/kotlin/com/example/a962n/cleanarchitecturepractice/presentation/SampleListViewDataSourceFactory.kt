package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository

class SampleListViewDataSourceFactory(private val repository: SampleListRepository) :DataSource.Factory<Int,SampleListItemView>(){

    val nowDataSource = MutableLiveData<SampleListViewDataSource>()

    override fun create(): DataSource<Int, SampleListItemView> {
        //HACK
        //データの再読み込みなどをする場合にdataSource#invalidateを実施するが
        //invalidateを実行するとFactory#createが再度実行され、DataSourceが再生成を求めるので本メソッドでは
        //必ず生成処理を行う(DataSourceの使い回しはしてはいけない)
        val dataSource = SampleListViewDataSource(repository)
        nowDataSource.postValue(dataSource)
        return dataSource
    }
}