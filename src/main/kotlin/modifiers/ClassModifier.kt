package modifiers

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import java.io.File


class ClassModifier {

    companion object {
        fun convertToRecord(
            text: String, project: Project, psiFile: PsiFile, file: File, fields: List<String>
        ) {
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

            val reduce: String = fileContentReplaced.map { s -> "$s\n" }.reduce { acc, s -> acc + s }

            val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(project)
            val newPsiFile: PsiFile = psiFileFactory.createFileFromText(psiFile.name, psiFile.fileType, reduce)

            val parent = psiFile.parent!!

            WriteCommandAction.runWriteCommandAction(project) {
                psiFile.delete()
                parent.add(newPsiFile)
            }
        }
    }

}
