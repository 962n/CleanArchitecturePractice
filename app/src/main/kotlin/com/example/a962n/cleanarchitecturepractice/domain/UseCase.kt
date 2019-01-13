package com.example.a962n.cleanarchitecturepractice.domain

import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means than any use
 * case in the application should implement this contract).
 *
 * By convention each [UseCase] implementation will execute its job in a background thread
 * (kotlin coroutine) and will post the result in the UI thread.
 */
abstract class UseCase<out Type, in Params> where Type : Any {

    abstract fun run(params: Params): Either<Failure, Type>

    operator fun invoke(params: Params): Either<Failure, Type> {
        return run(params)
    }

    class None
}
