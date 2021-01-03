package io.heapy.komodo.di

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError
import kotlin.reflect.typeOf

/**
 * Nice to have for qualifier support without extra methods calls.
 *
 * https://youtrack.jetbrains.com/issue/KT-29919
 */
class GenericTypeAnnotationTest {
    @Target(AnnotationTarget.TYPE)
    annotation class Foo

    @Test
    fun `test annotation preserved`() {
        assertThrows<AssertionFailedError> {
            assertEquals(1, typeOf<@Foo String>().annotations.size)
        }
    }
}
