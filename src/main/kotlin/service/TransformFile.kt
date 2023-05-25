package service

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import io.ktor.util.reflect.*
import modifiers.ClassModifier.Companion.convertToRecord
import modifiers.DeleteModifier.Companion.deleteFields
import modifiers.DeleteModifier.Companion.deleteMethod
import modifiers.RenameModifier.Companion.renameMethod
import java.io.File


class TransformFile {

    fun replaceClassToRecord(psiFile: PsiFile, project: Project, file: File) {
        if (psiFile.instanceOf(PsiJavaFile::class)) {
            val psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass::class.java)
            psiClass!!.methods.forEach { psiMethod: PsiMethod? ->
                when {
                    psiMethod?.name?.startsWith("set") == true -> deleteMethod(project, psiMethod)
                    psiMethod?.name?.matches(Regex("equals|hashCode")) == true -> deleteMethod(project, psiMethod)

                    psiMethod?.name?.startsWith("get") == true -> renameMethod(psiMethod, project)
                }
            }
            val fieldNames = psiClass.fields.map {
                "${annotations(it)} ${it.type.presentableText} ${it.name}"
            }
            deleteFields(project, psiClass)

            convertToRecord(psiClass.text, project, psiFile, file, fieldNames)
        }
    }

    private fun annotations(it: PsiField): String {
        val result = it.annotations.filterNotNull().mapNotNull { ann -> ann.text }
        return if (result.isEmpty()) ""
        else result.reduce { ann, ann2 -> "$ann\n$ann2" }
    }


    fun replaceClassToRecord(file: File, project: Project) {
        val virtualFile: VirtualFile? = LocalFileSystem.getInstance().findFileByNioFile(file.toPath())

        val psiManager = PsiManager.getInstance(project)
        val psiFile = psiManager.findFile(virtualFile!!) as PsiJavaFile
        replaceClassToRecord(psiFile, project, file)
    }

}