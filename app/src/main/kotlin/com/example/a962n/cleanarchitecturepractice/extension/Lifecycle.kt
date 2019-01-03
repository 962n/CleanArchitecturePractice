package com.example.a962n.cleanarchitecturepractice.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer


/**
 * LiveDataへオブザーバー(データ変更通知)の登録処理
 * @param liveData データ
 * @param body オブザーバー
 */
fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, Observer { body(it) })
