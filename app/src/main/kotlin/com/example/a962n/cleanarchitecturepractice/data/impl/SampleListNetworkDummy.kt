package com.example.a962n.cleanarchitecturepractice.data.impl

import com.example.a962n.cleanarchitecturepractice.util.NetworkHandler
import com.example.a962n.cleanarchitecturepractice.data.Either
import com.example.a962n.cleanarchitecturepractice.data.entity.SampleListEntity
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure
import com.example.a962n.cleanarchitecturepractice.data.exception.Failure.NetworkConnection
import com.example.a962n.cleanarchitecturepractice.data.impl.exception.SampleListFailure.DataReadFailure
import com.example.a962n.cleanarchitecturepractice.data.repository.SampleListRepository

class SampleListNetworkDummy constructor(val handler: NetworkHandler) : SampleListRepository {

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
        if (end > MAX_SIZE - 1){
            end = MAX_SIZE - 1
        }

        var list: MutableList<SampleListEntity> = mutableListOf()
        for (i in offset..end){
            list.add(SampleListEntity("HogeTarou$i"))
        }

        return Either.Right(list)
    }
}