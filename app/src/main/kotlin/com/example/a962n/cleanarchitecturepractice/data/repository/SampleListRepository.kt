package com.example.a962n.cleanarchitecturepractice.data.repository

import coreComponent.kotlin.Either
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure

interface SampleListRepository {
    fun list(offset: Int, limit: Int) : Either<Failure, List<SampleListEntity>>
}