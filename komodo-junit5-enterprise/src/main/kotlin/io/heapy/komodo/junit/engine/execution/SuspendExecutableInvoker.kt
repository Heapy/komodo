package io.heapy.komodo.junit.engine.execution

import io.heapy.komodo.junit.engine.execution.ExecutableInvoker.COROUTINE_SCOPE
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.platform.commons.util.ExceptionUtils
import org.junit.platform.commons.util.Preconditions
import org.junit.platform.commons.util.ReflectionUtils.isStatic
import org.junit.platform.commons.util.ReflectionUtils.makeAccessible
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

/**
 * TODO.
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
/**
 * @see org.junit.platform.commons.support.ReflectionSupport.invokeMethod
 */
fun invokeMethod(method: Method, target: Any?, vararg args: Any): Any? {
    Preconditions.notNull(method, "Method must not be null")
    Preconditions.condition(target != null || isStatic(method)
    ) { String.format("Cannot invoke non-static method [%s] on a null target.", method.toGenericString()) }

    try {
        val params = args.asList().dropLast(1)
        if (params.contains(ExecutableInvoker.TEST_COROUTINE_SCOPE)) {
            return runBlockingTest {
                val callArgs = params.map {
                    if (it == ExecutableInvoker.TEST_COROUTINE_SCOPE) this
                    else it
                }.toTypedArray()

                makeAccessible(method).kotlinFunction?.callSuspend(target, *callArgs)
            }
        } else if (params.contains(COROUTINE_SCOPE)) {
            return runBlocking {
                val callArgs = params.map {
                    if (it == ExecutableInvoker.COROUTINE_SCOPE) this
                    else it
                }.toTypedArray()

                makeAccessible(method).kotlinFunction?.callSuspend(target, *callArgs)
            }
        } else {
            return runBlocking {
                makeAccessible(method).kotlinFunction?.callSuspend(target, *params.toTypedArray())
            }
        }
    } catch (t: Throwable) {
        throw ExceptionUtils.throwAsUncheckedException(getUnderlyingCause(t))
    }
}

private fun getUnderlyingCause(t: Throwable): Throwable {
    return if (t is InvocationTargetException) {
        getUnderlyingCause(t.targetException)
    } else t
}
