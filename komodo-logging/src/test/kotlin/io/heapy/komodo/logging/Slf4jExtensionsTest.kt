package io.heapy.komodo.logging

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.slf4j.Logger

/**
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class Slf4jExtensionsTest {
    @Test
    fun `test error extension`() {
        val logger = mockk<Logger>(relaxUnitFun = true)
        every { logger.isErrorEnabled } answers { true }
        every { logger.error(any()) } answers { Unit }
        logger.error { "Error1" }
        verify(exactly = 1) { logger.error("Error1") }
    }
}
