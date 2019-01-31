package com.example.a962n.cleanarchitecturepractice.domain.impl

import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository
import com.example.a962n.cleanarchitecturepractice.domain.AsyncUseCase
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