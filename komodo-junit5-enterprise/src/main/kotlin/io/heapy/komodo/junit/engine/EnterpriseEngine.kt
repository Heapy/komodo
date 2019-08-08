package io.heapy.komodo.junit.engine

import kotlinx.coroutines.runBlocking
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.kotlinFunction

val KIEV_ENGINE_ID = "kotlin-kiev"
val KIEV_ENGINE_UID = UniqueId.forEngine(KIEV_ENGINE_ID)
val KIEV_ENGINE_NAME = "Kotlin Kiev"

class KotlinKievEngine : TestEngine {
    override fun getId() = KIEV_ENGINE_ID

    override fun discover(
        discoveryRequest: EngineDiscoveryRequest,
        uniqueId: UniqueId
    ): TestDescriptor {
        val root = EngineDescriptor(
            KIEV_ENGINE_UID,
            KIEV_ENGINE_NAME
        )

        discoveryRequest.getSelectorsByType(MethodSelector::class.java)
            .forEach { selector ->
                selector.javaMethod.kotlinFunction?.let {
                    if (it.isSuspend) {
                        root.addChild(MethodTestDescriptor(it, selector.javaClass.kotlin))
                    }
                }
            }

        return root
    }

    override fun execute(request: ExecutionRequest) {
        val engine = request.rootTestDescriptor
        val listener = request.engineExecutionListener
        listener.executionStarted(engine)
        engine.children.forEach { child ->
            if (child is MethodTestDescriptor) {
                listener.executionStarted(child)
                try {
                    runBlocking {
                        child.function.callSuspend(child.enclosureClass.createInstance())
                    }
                    listener.executionFinished(child, TestExecutionResult.successful())
                } catch (e: Throwable) {
                    listener.executionFinished(child, TestExecutionResult.failed(e))
                }
            }
        }
        listener.executionFinished(engine, TestExecutionResult.successful())
    }
}

class MethodTestDescriptor(
    val function: KFunction<*>,
    val enclosureClass: KClass<*>
) : AbstractTestDescriptor(
    KIEV_ENGINE_UID.append("method", function.name),
    "Kiev: ${function.name}"
) {
    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
}
