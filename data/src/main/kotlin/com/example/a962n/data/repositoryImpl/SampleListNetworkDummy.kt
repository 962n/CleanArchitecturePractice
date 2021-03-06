package com.example.a962n.data.repositoryImpl

import coreComponent.kotlin.functional.Either
import com.example.a962n.domain.entity.SampleListEntity
import com.example.a962n.domain.exception.Failure
import com.example.a962n.domain.exception.Failure.NetworkConnection
import com.example.a962n.domain.exception.SampleListFailure.DataReadFailure
import com.example.a962n.domain.repository.SampleListRepository
import com.example.a962n.data.NetworkHandler
import javax.inject.Inject

class SampleListNetworkDummy @Inject constructor(val handler: NetworkHandler) : SampleListRepository {

    companion object {
        private const val MAX_SIZE = 100
    }

    override fun list(offset: Int, limit: Int): Either<Failure, List<SampleListEntity>> {

        if (!handler.isConnected) {
            return Either.Left(NetworkConnection)
        }

        if (offset > MAX_SIZE - 1) {
            return Either.Left(DataReadFailure)
        }
        var end = offset + limit
        if (end > MAX_SIZE - 1) {
            end = MAX_SIZE - 1
        }

        val list: MutableList<SampleListEntity> = mutableListOf()
        for (i in offset..end) {
            list.add(SampleListEntity("HogeTarou$i"))
        }

        return Either.Right(list)
    }
}