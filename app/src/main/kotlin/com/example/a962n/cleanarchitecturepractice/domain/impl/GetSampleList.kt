package com.example.a962n.cleanarchitecturepractice.domain.impl

import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository
import com.example.a962n.cleanarchitecturepractice.domain.UseCase

class GetSampleList
constructor(private val repository: SampleListRepository) : UseCase<List<SampleListEntity>, GetSampleList.Param>() {

    override fun run(param: Param): Either<Failure, List<SampleListEntity>> {
        return repository.list(param.offset, param.limit)
    }

    data class Param(val offset: Int, val limit: Int)
}