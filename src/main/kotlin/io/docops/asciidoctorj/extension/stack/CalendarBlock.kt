package io.docops.asciidoctorj.extension.stack

import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor
import org.asciidoctor.extension.Contexts
import org.asciidoctor.extension.Name
import org.asciidoctor.extension.Reader
import java.util.*

@Name("calendar")
@Contexts(Contexts.LISTING)
class CalendarBlock : BlockProcessor() {
    override fun process(parent: StructuralNode, reader: Reader, attributes: MutableMap<String, Any>): Any {
        val planning: MutableMap<String, MutableList<String>> = HashMap()

        for (line in reader.readLines()) {
            assert(line.split(" ").toTypedArray().size > 1)
            val day = line.split(" ").toTypedArray()[0]
            val plan = line.substring(line.indexOf(" "))
            if (planning.containsKey(day) == false) planning[day] = ArrayList()
            val lines = planning[day]!!
            lines.add(plan)
        }
        val content: MutableList<String> = ArrayList()
        if (attributes.containsKey("title")) content.add("." + attributes["title"])
        content.addAll(
            Arrays.asList(
                "[cols=a7*,options=header]",
                "|===",
                "|M|T|W|T|F|S|S",
                ""
            )
        )
        val year = attributes["year"].toString().toInt()
        var month = attributes["month"].toString().toInt()
        month--
        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c[Calendar.DAY_OF_MONTH] = 1
        c[Calendar.YEAR] = year
        c[Calendar.MONTH] = month

        while (c[Calendar.DAY_OF_WEEK] != Calendar.MONDAY) {
            c.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (c[Calendar.MONTH] != month) {
            content.add("|")
            c.add(Calendar.DATE, 1)
        }

        while (c[Calendar.MONTH] == month) {
            val d = c[Calendar.DAY_OF_MONTH]
            content.add("|$d")
            if (planning.containsKey("" + d)) {
                val plan: List<String>? = planning["" + d]
                for (str in plan!!) {
                    content.add("")
                    content.add(str)
                    content.add("")
                }
            }
            content.add("")
            c.add(Calendar.DATE, 1)
        }

        while (c[Calendar.DAY_OF_WEEK] != Calendar.MONDAY) {
            content.add("|")
            c.add(Calendar.DAY_OF_YEAR, 1)
        }

        content.add("|===")

        val block = createBlock(parent, "open", null as String?)
        parseContent(block, content)
        return block


    }
}