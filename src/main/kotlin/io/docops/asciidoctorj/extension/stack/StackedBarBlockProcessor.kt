package io.docops.asciidoctorj.extension.stack

import io.docops.asciidoc.stackbar.StackedBarMaker
import io.docops.asciidoc.stackbar.model.StackModel
import org.asciidoctor.ast.Block
import org.asciidoctor.ast.ContentModel
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor
import org.asciidoctor.extension.Contexts
import org.asciidoctor.extension.Name
import org.asciidoctor.extension.Reader
import java.io.File

@Name("stackbar")
@Contexts(Contexts.LISTING)
@ContentModel(ContentModel.COMPOUND)
class StackedBarBlockProcessor: BlockProcessor() {
    override fun process(parent: StructuralNode, reader: Reader, attributes: MutableMap<String, Any>): Any {
        var filename = attributes.getOrDefault("2", "${System.currentTimeMillis()}_unk") as String
        val title = attributes.getOrDefault("3", "")
        val content = reader.read()
        val backend = parent.document.getAttribute("backend")
        var pdf = false
        if("pdf" == backend){
            pdf = true
            filename +="_pdf"

        }
        val st = StackedBarMaker(pdf)
        val list = strToStackedModels(content)
        val imgSrc = st.makeStackedBar(list, title.toString())
        val svg = File("${reader.dir}/images/${filename}.svg")
        svg.writeBytes(imgSrc.toByteArray())
        val blockAttrs = mutableMapOf<String, Any>(
            "role" to "docops.io.stackbar",
            "target" to "images/${filename}.svg",
            "alt" to "IMG not available",
            "title" to "Figure. $filename",
            "interactive-option" to "",
            "format" to "svg"

        )
        val argAttributes: MutableMap<String, Any> = HashMap()
        argAttributes["content_model"] = ":raw"
        val block: Block = createBlock(parent, "open", "", argAttributes, HashMap<Any, Any>())
        block.blocks.add(createBlock(parent, "image", ArrayList(), blockAttrs, HashMap()))
        if("pdf" == backend) {
            val lines = st.toLine(filename, list)
            val pdfBlock = createBlock(parent, "open", lines)
            parseContent(block, pdfBlock.lines)
        }
        return block
    }

    private fun strToStackedModels(str: String): MutableList<StackModel> {
        val result = mutableListOf<StackModel>()
        str.lines().forEach { line ->
            val items = line.split("|")
            result.add(StackModel(value = items[0].trim().toDouble(), description = items[1].trim(), fullDescription = items[2].trim()))
        }
        return result
    }
}