package io.heapy.komodo.core.concurent

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
internal class KomodoThreadFactoryTest {
    @Test
    fun `test default values`() {
        val factory = KomodoThreadFactory()
        val defaultThread = factory.newThread {}

        assertFalse(defaultThread.isDaemon)
        assertTrue(defaultThread.name.startsWith("komodo-"))
    }

    @Test
    fun `test custom factory`() {
        val factory = KomodoThreadFactory(true) { "test-$it" }
        val defaultThread = factory.newThread {}

        assertTrue(defaultThread.isDaemon)
        assertTrue(defaultThread.name.startsWith("test-"))
    }
}
