package com.example.a962n.cleanarchitecturepractice.util

import android.arch.lifecycle.*
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager

/**
 * ダイアログフラグメント表示コントロールクラス
 *
 * FragmentクラスはAndroid Frame Workの仕様上、バックグラウンドでの操作が禁止されています。
 * (バックグラウンドで操作すると強制停止する)
 *
 * 通常画面の場合は基本的にユーザーの操作によって遷移が行われるため、前述の仕様が問題になることは多くはありませんが、
 * DialogFragmentの場合は問題となることが多々あります。
 * (例えば、通信中にダイアログを表示し、通信完了時にダイアログを消去するなどの非同期処理です。)
 *
 * 本クラスはこの問題を解決するために用意しています。
 * ダイアログの表示or消去命令を出した際にライフサイクルオーナーの現在のライフサイクル状態をチェックし、
 * onPause状態などであれば、一度ペンディングし、再度onResume状態に復帰した際に表示or消去を実行します。
 *
 * ダイアログの表示処理、消去処理はそれが非同期かどうかに関わらず、本クラスを使用しておくとベターです。
 * ※ 一貫して、LiveDataのデータ変更通知をトリガーに非同期で表示や消去を行う場合はこの限りではありません。
 *
 * @param owner ライフサイクルオーナー
 */
class DialogController constructor(owner: LifecycleOwner) {

    /**
     * ダイアログのオペレーションクラス
     * 本クラスのインスタンスをチェックし、処理(表示or消去)実行する
     */
    private sealed class PendingOperation {
        /**
         * ダイアログ表示命令
         *
         * @param manager FragmentManager
         * @param tag 表示用のタグ
         * @param dialog 表示ダイアログ
         */
        data class Show(val manager: FragmentManager, val tag: String, val dialog: DialogFragment) : PendingOperation()

        /**
         * ダイアログ消去命令
         * @param manager 消去対象のダイアログを表示した際に使用したFragmentManager
         * @param tag 消去対象のダイアログを表示した際に使用したタグ
         */
        data class Dismiss(val manager: FragmentManager, val tag: String) : PendingOperation()
    }

    /** 保留リスト ライフサイクルオーナーがResume状態ではない場合に本リストに実行命令を追加する */
    private val pendingQueue: ArrayList<PendingOperation> = arrayListOf()

    /** ライフサイクルオーナーの現在のライフサイクル状態 */
    private var currentState: Lifecycle.State

    /** ライフサイクルをチェックするためのオブザーバー */
    private val observer: GenericLifecycleObserver = object : GenericLifecycleObserver {

        override fun onStateChanged(owner: LifecycleOwner?, event: Lifecycle.Event?) {

            owner?.let {
                currentState = it.lifecycle.currentState
            }

            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    dequeue()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    owner?.let {
                        it.lifecycle.removeObserver(this)
                        pendingQueue.clear()
                    }
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    init {
        currentState = owner.lifecycle.currentState
        owner.lifecycle.addObserver(observer)
    }

    /**
     * ダイアログ表示or消去処理を保留にします。
     * @param operation ダイアログのオペレーション
     */
    private fun enqueue(operation: PendingOperation) {
        pendingQueue.add(operation)
    }

    /**
     * 保留状態となっていたダイアログ表示or消去処理を実行します。
     */
    private fun dequeue() {
        for (operation in pendingQueue) {
            when (operation) {
                is PendingOperation.Show -> {
                    operation.dialog.show(operation.manager, operation.tag)
                }
                is PendingOperation.Dismiss -> {
                    this.dismissIfNeed(operation.manager, operation.tag)
                }
            }
        }
        pendingQueue.clear()
    }

    /**
     * ダイアログの消去処理
     * 対象のダイアログがFragmentManagerにアタッチされていない場合は消去処理は行わない
     */
    private fun dismissIfNeed(manager: FragmentManager, tag: String) {
        val f = manager.findFragmentByTag(tag)
        if (f is DialogFragment) {
            f.dismiss()
        }
    }

    /**
     * ダイアログの表示処理
     * ライフサイクルオーナーがonResume状態でない場合は処理を保留にします。
     *
     * @param manager FragmentManager
     * @param tag    表示用のタグ
     * @param dialog 表示するダイアログフラグメント
     */
    fun show(manager: FragmentManager?, tag: String, dialog: DialogFragment) {
        if (manager == null) {
            return
        }
        if (currentState == Lifecycle.State.RESUMED) {
            dialog.show(manager, tag)
            return
        }
        enqueue(PendingOperation.Show(manager, tag, dialog))
    }

    /**
     * ダイアログの消去処理
     * ライフサイクルオーナーがonResume状態でない場合は処理を保留にします。
     *
     * @param manager 消去対象のダイアログを表示した際に使用したFragmentManager
     * @param tag 消去対象のダイアログを表示した際に使用したタグ
     */
    fun dismiss(manager: FragmentManager?, tag: String) {

        //HACK 保留リストに対象のダイアログが存在すれば、保留リストから削除し、処理を終了する
        val deleteList = pendingQueue.filter { operation ->
            (operation is PendingOperation.Show && operation.tag == tag)
        }
        for (operation in deleteList) {
            pendingQueue.remove(operation)
        }
        if (deleteList.isNotEmpty()) {
            return
        }
        if (manager == null) {
            return
        }
        if (currentState == Lifecycle.State.RESUMED) {
            dismissIfNeed(manager, tag)
            return
        }
        enqueue(PendingOperation.Dismiss(manager, tag))
    }


}