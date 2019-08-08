package io.heapy.komodo.junit.engine

import kotlinx.coroutines.delay
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request
import org.junit.platform.testkit.engine.EngineTestKit
import kotlin.reflect.jvm.javaMethod

class KievEngineIntegrationTests {
    @Test
    fun `🆗 execute kiev kotlin engine`() {
        val discoveryRequest = request().selectors(DiscoverySelectors.selectMethod(
            KievEngineTest::class.java,
            KievEngineTest::`suspend test`.javaMethod
        )).build()
        val executionResults = EngineTestKit.execute(KotlinKievEngine(), discoveryRequest)

        executionResults.all().assertStatistics { it.started(2).finished(2).succeeded(2) }
        executionResults.tests().assertStatistics { it.started(1).finished(1).failed(0) }

        val testDescriptor = executionResults.tests().succeeded().list().first().testDescriptor

        assertAll(
            { assertEquals("Kiev: suspend test", testDescriptor.displayName) },
            { assertEquals("Kiev: suspend test", testDescriptor.legacyReportingName) },
            { assertTrue(testDescriptor is MethodTestDescriptor) }
        )
    }
}

class KievEngineTest {
    @Test
    suspend fun `suspend test`() {
        delay(10)
        assertEquals(
            "hello, world",
            "hello" + ", world"
        )
    }
}

fun main() {
    (1..1000).map {
        """
        @Test
        fun test$it() { 
            assertEquals(1, 1)
        }
        """.trimIndent()
    }.joinToString(separator = "") { "$it\n\n" }.also { println(it) }
}
