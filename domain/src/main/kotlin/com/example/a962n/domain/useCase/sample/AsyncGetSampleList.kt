package com.example.a962n.domain.useCase.sample

import coreComponent.kotlin.functional.Either
import com.example.a962n.domain.entity.SampleListEntity
import com.example.a962n.domain.exception.Failure
import com.example.a962n.domain.repository.SampleListRepository
import com.example.a962n.domain.useCase.core.AsyncUseCase
import java.lang.Thread.sleep

class AsyncGetSampleList
constructor(private val repository: SampleListRepository) : AsyncUseCase<List<SampleListEntity>, AsyncGetSampleList.Param>() {

    override fun run(params: Param, onResult: (Either<Failure, List<SampleListEntity>>) -> Unit) {
        val result = repository.list(params.offset, params.limit)
        sleep(1000)
        onResult(result)
    }

    data class Param(val offset: Int, val limit: Int)
}