package io.heapy.komodo.di

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError
import kotlin.reflect.jvm.reflect

/**
 * @author Ruslan Ibragimov
 * @since 1.0.0
 */
class LambdaReflectionTest {

    @Test
    fun `introspecting lambdas`() {
        val lambda = { a: String, b: Int -> a + b }

        // Since feature not yet implemented in Kotlin
        assertThrows<KotlinReflectionInternalError> {
            val result = lambda.reflect()?.call("Hello ", 42)
            assertEquals("Hello 42", result)
        }
    }
}
