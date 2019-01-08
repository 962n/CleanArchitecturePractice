package com.example.a962n.cleanarchitecturepractice

import android.arch.lifecycle.*

/**
 * 過去のデータ変更もオブザーバーに通知するLiveDataクラス
 *
 * 標準のLiveDataはライフサイクルオーナーがデータ変更通知受け取り可能になった場合、
 * 最新のデータのみをライフサイクルオーナーに通知する仕様になっています。
 *
 * これはどういうことかというと仮にLiveData<Int>とした場合に
 *    1)LiveData.value = 1 → 変更通知がされる
 *    2)ライフサイクルオーナーがinactiveに状態変化
 *    3)LiveData.value = 2 → 変更通知されない
 *    4)LiveData.value = 3 → 変更通知されない
 *    5)LiveData.value = 4 → 変更通知されない
 *    6)ライフサイクルオーナーがactiveに状態変化
 *    7)変更通知が行われるが最後のデータ4のみが通知される
 * となります。
 *
 * 表示のためのデータであれば、上記仕様で問題ありませんが、処理結果などの通知に使いたい場合はこの仕様では問題があります。
 * 本クラスはこの問題を解消するためのクラスとなります。
 *
 * 標準のLiveData同様にobserveメソッドを使用して、オブザーバーを登録してください。
 * ライフサイクルオーナーがデータ変更通知受け取り不可になった場合に、変更されたデータを履歴として保持します。
 * そして、ライフサイクルオーナーがデータ変更通知受け取り可能になった際にまとめてデータの変更通知が行われます。
 * オブザーバーの変更通知は変更が古かったものから順に通知が行われます。
 *
 */
class PendingLiveData<T> : MutableLiveData<T>() {

    companion object {
        private const val START_VERSION = -1
    }

    /** オブザーバーリスト */
    private var observerHolders: MutableList<ObserverHolder<T>> = mutableListOf()

    /** データの更新バージョン */
    private var version: Int = START_VERSION


    override fun setValue(value: T?) {
        version++
        for (holder in observerHolders) {
            holder.version = version
            holder.pendingIfNeed(version, value)
        }
        super.setValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            // ignore
            return
        }
        val exist = observerHolders.filter { predicate -> predicate.outerObserver == observer && predicate == owner }
        if (exist.isNotEmpty()) {
            //ignore
            return
        }
        val holder = ObserverHolder(owner, observer, version)
        observerHolders.add(holder)
        owner.lifecycle.addObserver(holder.lifecycleObserver)
        super.observe(owner, holder.innerObserver)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        var deletes = observerHolders.filter { predicate -> predicate.owner == owner }
        observerHolders.removeAll(deletes)
        for (delete in deletes) {
            delete.owner.lifecycle.removeObserver(delete.lifecycleObserver)
        }
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<T>) {
        var deletes = observerHolders.filter { predicate -> predicate.outerObserver == observer }
        observerHolders.removeAll(deletes)
        for (delete in deletes) {
            delete.owner.lifecycle.removeObserver(delete.lifecycleObserver)
            super.removeObserver(delete.innerObserver)
        }
    }

    /**
     * オブザーバーのWrapperクラス
     * @param owner ライフサイクルオーナー
     * @param outerObserver 外部オブザーバー
     * @param version データ変更バージョン
     */
    private inner class ObserverHolder<T>
    constructor(val owner: LifecycleOwner, val outerObserver: Observer<T>, var version: Int) {

        val innerObserver = Observer<T> {
            // HACK 最新の更新バージョンのデータをはじく(引数itが最新のデータのため)
            val withoutLatest = pendingData.filterKeys { key -> key != version }.toSortedMap()
            for ((_, value) in withoutLatest) {
                outerObserver.onChanged(value)
            }
            pendingData.clear()
            outerObserver.onChanged(it)
        }
        val lifecycleObserver = object : GenericLifecycleObserver {
            override fun onStateChanged(owner: LifecycleOwner?, event: Lifecycle.Event?) {
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        owner?.let {
                            owner.lifecycle.removeObserver(this)
                            observerHolders.removeAll { holder -> holder.innerObserver == innerObserver }
                        }
                    }
                    else -> {
                        //do nothing
                    }
                }
            }
        }

        private var pendingData: MutableMap<Int, T?> = mutableMapOf()

        /**
         * Active状態かどうか
         * @return true:Active状態/false:Active状態ではない
         */
        private fun shouldBeActive(): Boolean {
            return owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }

        /**
         * 変更されたデータのペンディング処理
         * @param version データ変更バージョン
         * @param data ペンディングしたいデータ
         */
        fun pendingIfNeed(version: Int, data: T?) {
            if (!shouldBeActive()) {
                pendingData[version] = data
            }
        }
    }

}