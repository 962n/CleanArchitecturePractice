package com.example.a962n.domain.repository

import coreComponent.kotlin.functional.Either
import com.example.a962n.domain.entity.SampleListEntity
import com.example.a962n.domain.exception.Failure

interface SampleListRepository {
    fun list(offset: Int, limit: Int) : Either<Failure, List<SampleListEntity>>
}