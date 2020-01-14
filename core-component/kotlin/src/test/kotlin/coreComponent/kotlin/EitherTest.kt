package coreComponent.kotlin

import coreComponent.kotlin.functional.Either
import org.junit.Assert
import org.junit.Test

class EitherTest {
    @Test
    fun testIs() {
        val either : Either<String, Int> = Either.Right(1)
        Assert.assertTrue(either.isRight)
        Assert.assertFalse(either.isLeft)
    }
}