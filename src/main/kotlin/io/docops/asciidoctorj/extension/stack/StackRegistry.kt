package io.docops.asciidoctorj.extension.stack

import org.asciidoctor.Asciidoctor
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry

class StackRegistry : ExtensionRegistry {
    override fun register(asciidoctor: Asciidoctor) {
        val registry = asciidoctor.javaExtensionRegistry()
        registry.block(StackedBarBlockProcessor::class.java)
        registry.block(CalendarBlock::class.java)
    }
}