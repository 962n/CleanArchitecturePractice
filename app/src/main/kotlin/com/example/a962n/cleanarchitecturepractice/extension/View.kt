package com.example.a962n.cleanarchitecturepractice.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

/**
 * LayoutInflaterを取得する。
 *
 * HACK:RecyclerView.Adapterクラスに対して、inflater生成のために毎回contextクラスを渡さなくて良いようにするため
 *
 * @return LayoutInflater LayoutInflaterクラス
 */
fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

/**
 * Layoutサイズが確定されたタイミングを検知する
 * (注)本メソッドに指定したコールバックは一度しかコールされません。
 *
 * @param listener レイアウト確定時のコールバック
 *
 */
fun View.addGlobalLayoutOnce(listener: () -> Unit) {

    val onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            listener()
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
}