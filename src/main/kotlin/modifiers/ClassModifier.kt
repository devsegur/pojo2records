package modifiers

import java.io.File

class ClassModifier {

    companion object {
        fun convertToRecord(text: String, file: File, fields: List<String>) {
            val fileContentReplaced = mutableListOf<String>()

            file.readLines().forEach {
                if (it.startsWith("package") or it.startsWith("import")) {
                    fileContentReplaced.add(it)
                }
            }

            text.split("\n").forEach {
                if (it.contains("public class")) {
                    val replace = it.replace("class", "record")
                    val fieldsReduced = fields.reduce { acc, s -> "$acc,$s" }
                    val split = replace.replace(" {", "($fieldsReduced) { ")
                    fileContentReplaced.add(split)
                } else {
                    fileContentReplaced.add(it)
                }
            }

            val reduce: String = fileContentReplaced.map { s -> "\n$s\n" }.reduce { acc, s -> acc + s }
            file.writeText(reduce)
        }
    }

}
