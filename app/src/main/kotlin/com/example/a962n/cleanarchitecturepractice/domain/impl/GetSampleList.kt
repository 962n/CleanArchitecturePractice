package com.example.a962n.cleanarchitecturepractice.domain.impl

import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository
import com.example.a962n.cleanarchitecturepractice.domain.UseCase

class GetSampleList
constructor(private val repository: SampleListRepository) : UseCase<List<SampleListEntity>, GetSampleList.Param>() {

    private var currentOffset = 0
    private val limit = 20

    override suspend fun run(params: Param): Either<Failure, List<SampleListEntity>> {
        when (params) {
            is Param.Refresh -> {
                currentOffset = 0
            }
            is Param.LoadMore -> {
                currentOffset += limit
            }
        }
        return repository.list(currentOffset, limit)
    }

    sealed class Param {
        object Refresh : Param()
        object LoadMore : Param()
    }
}