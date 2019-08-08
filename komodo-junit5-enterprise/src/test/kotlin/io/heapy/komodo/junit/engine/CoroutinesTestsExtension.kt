package io.heapy.komodo.junit.engine

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

interface UserApi {
    suspend fun getByEmail(email: String): String
}

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class CoroutinesEngineTest {
    @Test
    suspend fun `co sample test`(@MockK userApi: UserApi) {
        coEvery { userApi.getByEmail("foo") } returns "bar"
        assertEquals(userApi.getByEmail("foo"), "bar")
    }
}

@ExperimentalCoroutinesApi
class AnotherTest {

    var test = "Hello"

    @Test
    fun `sample test`() {
        assertEquals("hello, world", "hello" + ", world")
    }

    @Test
    suspend fun `sample suspend test`() {
        delay(1000)
        assertEquals("hello, world", "hello" + ", world")
    }

    @Test
    fun TestCoroutineScope.testFooWithLaunchAndDelay() {
        foo()
        advanceTimeBy(1_000)
    }

    fun CoroutineScope.foo() {
        launch {
            println(1)
            delay(1_000)
            println(2)
        }
    }
}






















class CoroutinesTests {
    @Test
    suspend fun `sample test`() {
        delay(2000)
        assertEquals(
            "hello, world",
            "hello" + ", world"
        )
    }
}








