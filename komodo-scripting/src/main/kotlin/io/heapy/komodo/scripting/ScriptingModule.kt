package io.heapy.komodo.scripting

import io.heapy.komodo.di.Binder
import io.heapy.komodo.di.Module

/**
 * Module that provides [KotlinScriptCompiler].
 *
 * @author Ruslan Ibragimov
 * @since 1.0
 */
class ScriptingModule : Module {
    fun assemble(binder: Binder) {
        binder.wire(KotlinScriptCompiler::class).with(DefaultKotlinScriptCompiler())
    }
}
