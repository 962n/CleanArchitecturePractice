package com.example.a962n.domain.useCase.sample

import coreComponent.kotlin.functional.Either
import com.example.a962n.domain.entity.SampleListEntity
import com.example.a962n.domain.exception.Failure
import com.example.a962n.domain.repository.SampleListRepository
import com.example.a962n.domain.useCase.core.UseCase

class GetSampleList
constructor(private val repository: SampleListRepository) : UseCase<List<SampleListEntity>, GetSampleList.Param>() {

    override fun run(param: Param): Either<Failure, List<SampleListEntity>> {
        return repository.list(param.offset, param.limit)
    }

    data class Param(val offset: Int, val limit: Int)
}