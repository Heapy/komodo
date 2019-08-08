/*
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package io.heapy.komodo.junit.engine.discovery.predicates

import org.junit.platform.commons.util.AnnotationUtils.isAnnotated
import org.junit.platform.commons.util.ReflectionUtils.isAbstract
import org.junit.platform.commons.util.ReflectionUtils.isPrivate
import org.junit.platform.commons.util.ReflectionUtils.isStatic
import java.lang.reflect.Method
import java.util.function.Predicate
import kotlin.reflect.jvm.kotlinFunction

/**
 * @since 5.0
 */
internal abstract class IsTestableMethod(
    private val annotationType: Class<out Annotation>,
    private val mustReturnVoid: Boolean
) : Predicate<Method> {

    override fun test(candidate: Method): Boolean {
        // Please do not collapse the following into a single statement.
        if (isStatic(candidate)) return false
        if (isPrivate(candidate)) return false
        if (isAbstract(candidate)) return false
        if (!isSuspend(candidate)) return false
        return isAnnotated(candidate, this.annotationType)
    }

    internal fun isSuspend(candidate: Method): Boolean {
        return candidate.kotlinFunction?.isSuspend ?: false
    }
}
