package com.example.a962n.domain.useCase.core

import coreComponent.kotlin.functional.Either
import com.example.a962n.domain.exception.Failure
import kotlinx.coroutines.*

/**
 * ユースケースの抽象クラス
 * 各ユースケースは本クラス or [UseCase] を継承して実装してください。
 * 各[AsyncUseCase]実装はそのジョブをバックグラウンドスレッドで実行してください。
 * 結果についてはUIスレッドに投稿します。
 * 同期的な処理を行いたい場合は[UseCase]を使用してください。
 */
abstract class AsyncUseCase<out Type, in Params> where Type : Any {

    abstract fun run(params: Params, onResult: (Either<Failure, Type>) -> Unit = {})

    operator fun invoke(params: Params, onResult: (Either<Failure, Type>) -> Unit = {}) {
        run(params) {
            CoroutineScope(Dispatchers.Main).launch {
                onResult(it)
            }
        }
    }

    class None
}